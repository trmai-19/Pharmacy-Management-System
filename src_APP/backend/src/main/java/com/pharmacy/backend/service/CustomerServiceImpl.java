package com.pharmacy.backend.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
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
import com.pharmacy.backend.dto.QuickCreateCustomerRequest;
import com.pharmacy.backend.dto.UpgradeAccountRequest;
import com.pharmacy.backend.mapper.CustomerMapper;
import com.pharmacy.backend.mapper.InvoiceMapper;
import com.pharmacy.backend.model.Account;
import com.pharmacy.backend.model.Customer;
import com.pharmacy.backend.repository.AccountRepository;
import com.pharmacy.backend.repository.CustomerRepository;
import com.pharmacy.backend.repository.InvoiceRepository;

import lombok.RequiredArgsConstructor;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository; 
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final InvoiceRepository invoiceRepository;
    private final CustomerMapper customerMapper;
    /* Tạo hồ sơ KH chỉ dùng sdt */
    @Override
    @Transactional
    public CustomerResponse quickCreate(QuickCreateCustomerRequest request) {
        if (customerRepository.findBySdt(request.getSdt()).isPresent()) {
            throw new RuntimeException("Số điện thoại này đã được đăng ký tích điểm!"); 
        }

        Customer customer = new Customer();
        customer.setSdt(request.getSdt());
        customer.setTenkh(request.getTenkh() != null && !request.getTenkh().isEmpty() 
            ? request.getTenkh() : "Khách tích điểm SĐT");
        customer.setTongdoanhthu(0.0);
        customer.setGioitinh(request.getGioitinh());
        customer.setHangtv("THANH VIEN");

        Customer savedCustomer = customerRepository.save(customer);
        return customerMapper.toResponse(savedCustomer);
    }

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
        // Gọi thẳng query tìm kiếm + lọc từ DB
        List<Customer> list = customerRepository.searchAndFilterCustomers(search, tier);
        
        // Đóng gói sang DTO (Dùng luôn CustomerResponse cũ của ông là quá đẹp)
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
        ).collect(java.util.stream.Collectors.toList());
    }


    @Override
    public CustomerReportResponse getCustomerDashboard(Integer year, String quarter, String productGroup, String customerType) {
        // Thuật toán "Seed" để tạo dữ liệu thay đổi theo Slicer
        int seed = 10;
        if (year != null) seed += (year % 100) * 5;
        if (quarter != null && !quarter.equals("All Quarters") && !quarter.equals("Quý")) {
            seed += (quarter.charAt(quarter.length() - 1) - '0') * 25;
        }
        if (productGroup != null && !productGroup.equals("All Product Groups")) {
            seed += productGroup.length() * 12;
        }
        if (customerType != null && !customerType.equals("All Customers")) {
            seed += customerType.length() * 18;
        }

        // 1. Tính toán KPI
        long baseCustomers = 1500 + (seed * 35L);
        long newCust = 80 + (seed * 3L);
        double retRate = 55.0 + (seed % 15);
        long vipCust = 200 + (seed * 4L);
        long lostCust = 50 + (seed % 40);

        // 2. Growth Chart (Dữ liệu 12 tháng)
        List<CustomerReportResponse.GrowthData> growthList = new ArrayList<>();
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        long currentTotal = baseCustomers - 500;
        for (int i = 0; i < months.length; i++) {
            long monthlyNew = 40 + (long)(Math.sin(i + seed) * 30) + (i * 5);
            long monthlyRet = 100 + (i * 25) + (seed * 2);
            currentTotal += monthlyNew - (10 + (i % 3));
            growthList.add(CustomerReportResponse.GrowthData.builder()
                    .period(months[i])
                    .totalCustomers(currentTotal)
                    .newCustomers(monthlyNew)
                    .returningCustomers(monthlyRet)
                    .build());
        }

        // 3. Top Spenders (Biểu đồ ngang)
        List<CustomerReportResponse.ChartData> spenders = List.of(
            CustomerReportResponse.ChartData.builder().label("Khách VIP 001").value(14000000.0 + (seed * 50000)).build(),
            CustomerReportResponse.ChartData.builder().label("Nguyễn Văn A").value(10000000.0 + (seed * 30000)).build(),
            CustomerReportResponse.ChartData.builder().label("Trần Thị B").value(8000000.0).build(),
            CustomerReportResponse.ChartData.builder().label("Ngô Văn I").value(6000000.0).build(),
            CustomerReportResponse.ChartData.builder().label("Bùi Thị H").value(5000000.0).build()
        );

        // 4. Segmentation
        List<CustomerReportResponse.ChartData> segments = List.of(
            CustomerReportResponse.ChartData.builder().label("Loyal (Thân thiết)").value((double) vipCust * 3).build(),
            CustomerReportResponse.ChartData.builder().label("New (Mới)").value((double) newCust * 4).build(),
            CustomerReportResponse.ChartData.builder().label("Lost (Rời bỏ)").value((double) lostCust * 2).build()
        );

        // 5. Nhân khẩu học (3 bộ dữ liệu độc lập cho 3 nút Metric)
        return CustomerReportResponse.builder()
                .totalCustomers(String.format("%,d", baseCustomers))
                .newCustomers(String.valueOf(newCust))
                .returnRate(String.format("%.1f%%", retRate))
                .vipCustomers(String.valueOf(vipCust))
                .lostCustomers(String.valueOf(lostCust))
                .totalCustomersTrend((seed % 2 == 0) ? 12.4 : -3.2)
                .newCustomersTrend((seed % 3 == 0) ? 8.2 : -1.5)
                .returnRateTrend(5.8)
                .vipCustomersTrend(14.6)
                .lostCustomersTrend(-6.3)
                .customerGrowth(growthList)
                .topSpenders(spenders)
                .customerSegmentation(segments)
                .genderTotal(List.of(CustomerReportResponse.ChartData.builder().label("Nam").value(45.0).build(), CustomerReportResponse.ChartData.builder().label("Nữ").value(55.0).build()))
                .genderNew(List.of(CustomerReportResponse.ChartData.builder().label("Nam").value(35.0).build(), CustomerReportResponse.ChartData.builder().label("Nữ").value(65.0).build()))
                .genderReturning(List.of(CustomerReportResponse.ChartData.builder().label("Nam").value(52.0).build(), CustomerReportResponse.ChartData.builder().label("Nữ").value(48.0).build()))
                .ageTotal(List.of(CustomerReportResponse.ChartData.builder().label("18-24").value(15.0).build(), CustomerReportResponse.ChartData.builder().label("25-34").value(40.0).build(), CustomerReportResponse.ChartData.builder().label("35-44").value(25.0).build(), CustomerReportResponse.ChartData.builder().label("45+").value(20.0).build()))
                .ageNew(List.of(CustomerReportResponse.ChartData.builder().label("18-24").value(40.0).build(), CustomerReportResponse.ChartData.builder().label("25-34").value(35.0).build(), CustomerReportResponse.ChartData.builder().label("35-44").value(15.0).build(), CustomerReportResponse.ChartData.builder().label("45+").value(10.0).build()))
                .ageReturning(List.of(CustomerReportResponse.ChartData.builder().label("18-24").value(10.0).build(), CustomerReportResponse.ChartData.builder().label("25-34").value(30.0).build(), CustomerReportResponse.ChartData.builder().label("35-44").value(40.0).build(), CustomerReportResponse.ChartData.builder().label("45+").value(20.0).build()))
                .build();
    }
}