package com.example.Insurance.services;

public interface QuotationPdfService {
    byte[] generateQuotationPdf(Long quotationId);
}
