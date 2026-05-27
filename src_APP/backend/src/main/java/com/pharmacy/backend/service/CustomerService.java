package com.pharmacy.backend.service;

import com.pharmacy.backend.dto.CreateCustomerRequest;
import com.pharmacy.backend.dto.CustomerReportResponse;
import com.pharmacy.backend.dto.CustomerResponse;
import com.pharmacy.backend.dto.CustomerStatsResponse;
import com.pharmacy.backend.dto.InvoiceResponse;
import com.pharmacy.backend.dto.UpgradeAccountRequest;
import com.pharmacy.backend.dto.QuickCreateCustomerRequest;
import java.util.List;
public interface CustomerService {
    CustomerResponse findBySdt(String sdt);
    List<CustomerResponse> searchCustomers(String keyword);
    CustomerResponse createCustomer(CreateCustomerRequest request);
    CustomerResponse quickCreate(QuickCreateCustomerRequest request);
    void upgradeToAccount(String makh, UpgradeAccountRequest request);
    List<InvoiceResponse> getPurchaseHistory(String makh);
    CustomerStatsResponse getCustomerStats();
    List<CustomerResponse> getCustomerList(String search, String tier);
    CustomerReportResponse getCustomerDashboard(Integer year, String quarter, String productGroup, String customerType);
}