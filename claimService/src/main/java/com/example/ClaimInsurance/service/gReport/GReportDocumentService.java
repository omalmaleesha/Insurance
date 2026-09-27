package com.example.ClaimInsurance.service.gReport;

import com.example.ClaimInsurance.entities.ClaimDocument;

public interface GReportDocumentService {
    String extractText(ClaimDocument document);
}