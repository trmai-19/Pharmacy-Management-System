package com.pharmacy.backend.service;

import com.pharmacy.backend.dto.InvoiceItemResponse;
import com.pharmacy.backend.dto.InvoiceRequest;
import com.pharmacy.backend.dto.InvoiceResponse;
import java.util.List;

public interface InvoiceService {
    InvoiceResponse createInvoice(InvoiceRequest request);
    List<InvoiceItemResponse> getInvoiceDetails(String mahd);
}