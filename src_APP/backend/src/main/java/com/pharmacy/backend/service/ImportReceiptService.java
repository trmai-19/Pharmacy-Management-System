package com.pharmacy.backend.service;

import com.pharmacy.backend.dto.ImportReceiptDetailResponse;
import com.pharmacy.backend.dto.ImportReceiptRequest;
import com.pharmacy.backend.dto.ImportReceiptResponse;

import java.util.List;

public interface ImportReceiptService {
    List<ImportReceiptResponse> getAllImportReceipts(String search);
    ImportReceiptResponse createImportReceipt(ImportReceiptRequest request);
    List<ImportReceiptDetailResponse> getDetailsByMapn(String mapn);
}