package com.pharmacy.backend.service;

import com.pharmacy.backend.dto.ReturnReceiptRequest;
import com.pharmacy.backend.dto.ReturnReceiptResponse;

public interface ReturnReceiptService {
    ReturnReceiptResponse createReturnReceipt(ReturnReceiptRequest request);
}