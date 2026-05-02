package com.pharmacy.backend.service;

import com.pharmacy.backend.dto.CreateCustomerRequest;
import com.pharmacy.backend.dto.CustomerResponse;
import com.pharmacy.backend.dto.InvoiceResponse;
import com.pharmacy.backend.dto.QuickCreateCustomerRequest;
import com.pharmacy.backend.dto.UpgradeAccountRequest;
import java.util.List;
public interface CustomerService {
    CustomerResponse findBySdt(String sdt);
    CustomerResponse createCustomer(CreateCustomerRequest request);
    CustomerResponse quickCreate(QuickCreateCustomerRequest request);
    void upgradeToAccount(String makh, UpgradeAccountRequest request);
    List<InvoiceResponse> getPurchaseHistory(String makh);
}