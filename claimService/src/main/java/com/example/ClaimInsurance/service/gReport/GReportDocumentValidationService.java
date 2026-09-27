package com.example.ClaimInsurance.service.gReport;

import com.example.ClaimInsurance.service.gReport.impl.GReportDocumentValidationResult;
import com.example.ClaimInsurance.utils.types.ClaimDocumentType;

public interface GReportDocumentValidationService {
    GReportDocumentValidationResult validate(
            ClaimDocumentType documentType,
            String extractedText
    );
}