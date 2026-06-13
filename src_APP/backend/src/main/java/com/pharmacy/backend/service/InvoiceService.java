package com.pharmacy.backend.service;

import com.pharmacy.backend.dto.InvoiceRequest;
import com.pharmacy.backend.dto.InvoiceResponse;
import java.util.List;
import com.pharmacy.backend.dto.InvoiceListResponse;

public interface InvoiceService {
    InvoiceResponse createInvoice(InvoiceRequest request);
    InvoiceResponse getInvoiceById(String mahd);
    List<InvoiceListResponse> getAllInvoices(String search);
    InvoiceResponse updateInvoiceStatus(String mahd, String status);
} 