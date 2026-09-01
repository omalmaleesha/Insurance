package com.example.Insurance.service.gReport;

import com.example.Insurance.entities.ClaimDocument;

public interface GReportDocumentService {
    String extractText(ClaimDocument document);
}