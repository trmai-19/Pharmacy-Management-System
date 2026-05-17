package com.pharmacy.backend.service;

import com.pharmacy.backend.dto.ImportReceiptRequest;
import com.pharmacy.backend.dto.ImportReceiptResponse;
import com.pharmacy.backend.model.ImportReceiptDetail;

import java.util.List;

public interface ImportReceiptService {
    List<ImportReceiptResponse> getAllImportReceipts();
    ImportReceiptResponse createImportReceipt(ImportReceiptRequest request);
    List<ImportReceiptDetail> getDetailsByMapn(String mapn);
}