package com.example.ClaimInsurance.service.gReport;

import com.example.ClaimInsurance.dto.StructuredClaimDocument;

public interface GReportStructuredExtractionService {
    StructuredClaimDocument extract(String documentText);

}