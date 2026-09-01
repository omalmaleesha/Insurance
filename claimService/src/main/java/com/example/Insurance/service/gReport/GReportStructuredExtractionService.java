package com.example.Insurance.service.gReport;

import com.example.Insurance.dto.StructuredClaimDocument;

public interface GReportStructuredExtractionService {
    StructuredClaimDocument extract(String documentText);

}