package com.pharmacy.backend.service;

import com.pharmacy.backend.dto.ReturnReceiptRequest;
import com.pharmacy.backend.dto.ReturnReceiptResponse;
import com.pharmacy.backend.dto.ReturnReceiptListResponse;
import java.util.List;

public interface ReturnReceiptService {
    ReturnReceiptResponse createReturnReceipt(ReturnReceiptRequest request);
    List<ReturnReceiptListResponse> getAllReturnReceipts(String search); // API Số 3
    ReturnReceiptResponse getReturnReceiptById(String maptKh);          // API Số 4
}