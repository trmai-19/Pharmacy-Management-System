package com.pharmacy.backend.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pharmacy.backend.dto.CreateCustomerRequest;
import com.pharmacy.backend.dto.CustomerReportResponse;
import com.pharmacy.backend.dto.CustomerResponse;
import com.pharmacy.backend.dto.CustomerStatsResponse;
import com.pharmacy.backend.dto.InvoiceResponse;
import com.pharmacy.backend.dto.UpgradeAccountRequest;
import com.pharmacy.backend.mapper.CustomerMapper;
import com.pharmacy.backend.mapper.InvoiceMapper;
import com.pharmacy.backend.model.Account;
import com.pharmacy.backend.model.Customer;
import com.pharmacy.backend.repository.AccountRepository;
import com.pharmacy.backend.repository.CustomerRepository;
import com.pharmacy.backend.repository.InvoiceRepository;
import com.pharmacy.backend.repository.ChartProjection;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository; 
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final InvoiceRepository invoiceRepository;
    private final CustomerMapper customerMapper;

    @Override
    @Transactional
    public void upgradeToAccount(String makh, UpgradeAccountRequest request) {
        Customer customer = customerRepository.findById(makh)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ khách hàng!"));

        if (customer.getMatk() != null) {
            throw new RuntimeException("Khách hàng đã có tài khoản!");
        }

        String rawPassword = UUID.randomUUID().toString().substring(0, 8);

        Account account = new Account();
        account.setSdt(customer.getSdt());
        account.setPassword(passwordEncoder.encode(rawPassword)); 
        account.setVaitro("CUSTOMER");
        account.setEmail(request.getEmail());
        account.setTrangthai("ACTIVE");
        account.setFirstLogin(true);
        account.setNgaytao(new Date());
        accountRepository.save(account);

        customer.setMatk(account.getMatk());
        customerRepository.save(customer);

        emailService.sendAccountCreationEmail(request.getEmail(), customer.getSdt(), rawPassword);
    }

    @Override
    public List<CustomerResponse> searchCustomers(String keyword) {
        List<Customer> customers = customerRepository.searchByTenkhOrSdt(keyword);
        return customers.stream()
                .map(customerMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CustomerResponse findBySdt(String sdt) {
        Customer customer = customerRepository.findBySdt(sdt)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng với SĐT: " + sdt));
        return customerMapper.toResponse(customer);
    }

    @Override
    @Transactional
    public CustomerResponse createCustomer(CreateCustomerRequest request) {
        if (customerRepository.findBySdt(request.getSdt()).isPresent()) {
            throw new RuntimeException("Số điện thoại này đã được đăng ký!");
        }

        Customer newCustomer = new Customer();
        customerMapper.updateCustomerFromRequest(newCustomer, request);
        return customerMapper.toResponse(customerRepository.save(newCustomer));
    }

    @Override
    public List<InvoiceResponse> getPurchaseHistory(String makh) {
        if (!customerRepository.existsById(makh)) {
            throw new RuntimeException("Không tìm thấy khách hàng với mã: " + makh);
        }
        
        LocalDateTime twoYearsAgo = LocalDateTime.now().minusYears(2);
        
        return invoiceRepository.findPurchaseHistory(makh, twoYearsAgo)
                .stream()
                .map(InvoiceMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CustomerStatsResponse getCustomerStats() {
        long total = customerRepository.count();
        long diamond = customerRepository.countByHangtv("Kim Cương");
        long gold = customerRepository.countByHangtv("Vàng");
        long silver = customerRepository.countByHangtv("Bạc");

        return CustomerStatsResponse.builder()
                .totalCustomers(total)
                .diamondCustomers(diamond)
                .goldCustomers(gold)
                .silverCustomers(silver)
                .build();
    }

    @Override
    public List<CustomerResponse> getCustomerList(String search, String tier) {
        List<Customer> list = customerRepository.searchAndFilterCustomers(search, tier);
        
        return list.stream().map(c -> CustomerResponse.builder()
                .makh(c.getMakh())
                .tenkh(c.getTenkh())
                .gioitinh(c.getGioitinh())
                .ngaysinh(c.getNgaysinh())
                .sdt(c.getSdt())
                .tongdoanhthu(c.getTongdoanhthu())
                .diemtichluy(c.getDiemtichluy())
                .hangtv(c.getHangtv())
                .build()
        ).collect(Collectors.toList());
    }

    @Override
    public CustomerReportResponse getCustomerDashboard(Integer year, String quarter, String productGroup, String customerType) {
        try {
            int paramYear = (year != null) ? year : 0;
            int paramQuarter = 0;
            if (quarter != null && quarter.matches("Q[1-4]")) {
                paramQuarter = Integer.parseInt(quarter.substring(1));
            }
            String pGroup = (productGroup != null) ? productGroup : "All Product Groups";
            String cType = (customerType != null) ? customerType : "All Customers";

            // 1. Kéo KPI thực tế từ Database
            Long totalCust = customerRepository.countActiveCustomers(paramYear, paramQuarter, pGroup, cType);
            Long newCust = customerRepository.countNewCustomers(paramYear, paramQuarter, pGroup, cType);
            Long returningCust = customerRepository.countReturningCustomers(paramYear, paramQuarter, pGroup, cType);
            Long vipCust = customerRepository.countVipCustomers(paramYear, paramQuarter, pGroup, cType);
            Long lostCust = customerRepository.countLostCustomers(paramYear, paramQuarter, pGroup, cType);

            totalCust = (totalCust != null) ? totalCust : 0L;
            newCust = (newCust != null) ? newCust : 0L;
            returningCust = (returningCust != null) ? returningCust : 0L;
            vipCust = (vipCust != null) ? vipCust : 0L;
            lostCust = (lostCust != null) ? lostCust : 0L;

            double returnRate = (totalCust > 0) ? ((double) returningCust / totalCust) * 100.0 : 0.0;

            // 2. Tính toán kỳ trước để làm Trend
            int prevYear = paramYear;
            int prevQuarter = paramQuarter;
            if (paramYear > 0) {
                if (paramQuarter == 0) { 
                    prevYear = paramYear - 1; 
                } else {
                    if (paramQuarter == 1) { 
                        prevQuarter = 4; prevYear = paramYear - 1; 
                    } else { 
                        prevQuarter = paramQuarter - 1; 
                    }
                }
            }

            Long prevTotal = customerRepository.countActiveCustomers(prevYear, prevQuarter, pGroup, cType);
            Long prevNew = customerRepository.countNewCustomers(prevYear, prevQuarter, pGroup, cType);
            Long prevReturning = customerRepository.countReturningCustomers(prevYear, prevQuarter, pGroup, cType);
            Long prevVip = customerRepository.countVipCustomers(prevYear, prevQuarter, pGroup, cType);
            Long prevLost = customerRepository.countLostCustomers(prevYear, prevQuarter, pGroup, cType);

            prevTotal = (prevTotal != null) ? prevTotal : 0L;
            prevNew = (prevNew != null) ? prevNew : 0L;
            prevReturning = (prevReturning != null) ? prevReturning : 0L;
            prevVip = (prevVip != null) ? prevVip : 0L;
            prevLost = (prevLost != null) ? prevLost : 0L;

            double prevReturnRate = (prevTotal > 0) ? ((double) prevReturning / prevTotal) * 100.0 : 0.0;

            // Tính % Trend thực tế
            double totalTrend = calculateTrend(totalCust, prevTotal);
            double newTrend = calculateTrend(newCust, prevNew);
            double returnRateTrend = returnRate - prevReturnRate;
            double vipTrend = calculateTrend(vipCust, prevVip);
            double lostTrend = calculateTrend(lostCust, prevLost);

            // 3. Kéo dữ liệu đồ thị từ CSDL
            List<Object[]> growthRaw = customerRepository.getCustomerGrowth(paramYear, paramQuarter, pGroup, cType);
            List<CustomerReportResponse.GrowthData> growthList = new ArrayList<>();
            for (Object[] obj : growthRaw) {
                growthList.add(CustomerReportResponse.GrowthData.builder()
                        .period((String) obj[0])
                        .totalCustomers(((Number) obj[1]).longValue())
                        .newCustomers(((Number) obj[2]).longValue())
                        .returningCustomers(((Number) obj[3]).longValue())
                        .build());
            }

            List<ChartProjection> topSpendersRaw = customerRepository.getTopSpendersReport(paramYear, paramQuarter, pGroup, cType);
            List<CustomerReportResponse.ChartData> spenders = topSpendersRaw.stream().map(p -> 
                CustomerReportResponse.ChartData.builder().label(p.getLabel()).value(p.getValue().doubleValue()).build()
            ).collect(Collectors.toList());

            // Tái sử dụng dữ liệu đã có cho Segmentation thay vì query lại
            List<CustomerReportResponse.ChartData> segments = List.of(
                CustomerReportResponse.ChartData.builder().label("Loyal (Thân thiết)").value((double) vipCust).build(),
                CustomerReportResponse.ChartData.builder().label("New (Mới)").value((double) newCust).build(),
                CustomerReportResponse.ChartData.builder().label("Lost (Rời bỏ)").value((double) lostCust).build()
            );

            // Fetch dữ liệu Nhân khẩu học tương ứng với 3 Metric
            List<CustomerReportResponse.ChartData> genderTotal = mapChartData(customerRepository.getCustomerGenderStats(paramYear, paramQuarter, pGroup, cType, "TOTAL"));
            List<CustomerReportResponse.ChartData> genderNew = mapChartData(customerRepository.getCustomerGenderStats(paramYear, paramQuarter, pGroup, cType, "NEW"));
            List<CustomerReportResponse.ChartData> genderReturning = mapChartData(customerRepository.getCustomerGenderStats(paramYear, paramQuarter, pGroup, cType, "RETURNING"));

            List<CustomerReportResponse.ChartData> ageTotal = mapChartData(customerRepository.getCustomerAgeStats(paramYear, paramQuarter, pGroup, cType, "TOTAL"));
            List<CustomerReportResponse.ChartData> ageNew = mapChartData(customerRepository.getCustomerAgeStats(paramYear, paramQuarter, pGroup, cType, "NEW"));
            List<CustomerReportResponse.ChartData> ageReturning = mapChartData(customerRepository.getCustomerAgeStats(paramYear, paramQuarter, pGroup, cType, "RETURNING"));

            return CustomerReportResponse.builder()
                    .totalCustomers(String.format("%,d", totalCust))
                    .newCustomers(String.valueOf(newCust))
                    .returnRate(String.format("%.1f%%", returnRate))
                    .vipCustomers(String.valueOf(vipCust))
                    .lostCustomers(String.valueOf(lostCust))
                    
                    .totalCustomersTrend(Math.round(totalTrend * 100.0) / 100.0)
                    .newCustomersTrend(Math.round(newTrend * 100.0) / 100.0)
                    .returnRateTrend(Math.round(returnRateTrend * 100.0) / 100.0)
                    .vipCustomersTrend(Math.round(vipTrend * 100.0) / 100.0)
                    .lostCustomersTrend(Math.round(lostTrend * 100.0) / 100.0)
                    
                    .customerGrowth(growthList)
                    .topSpenders(spenders)
                    .customerSegmentation(segments)
                    
                    .genderTotal(genderTotal).genderNew(genderNew).genderReturning(genderReturning)
                    .ageTotal(ageTotal).ageNew(ageNew).ageReturning(ageReturning)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi truy xuất dữ liệu Customer Dashboard: " + e.getMessage());
        }
    }

    private double calculateTrend(double current, double previous) {
        if (previous == 0) return current > 0 ? 100.0 : 0.0;
        return ((current - previous) / previous) * 100.0;
    }

    private List<CustomerReportResponse.ChartData> mapChartData(List<ChartProjection> rawList) {
        return rawList.stream().map(p -> 
            CustomerReportResponse.ChartData.builder()
                .label(p.getLabel())
                .value(p.getValue().doubleValue())
                .build()
        ).collect(Collectors.toList());
    }
}