package com.example.Insurance.service.gReport;

import com.example.Insurance.service.gReport.impl.GReportDocumentValidationResult;
import com.example.Insurance.utils.types.ClaimDocumentType;

public interface GReportDocumentValidationService {
    GReportDocumentValidationResult validate(
            ClaimDocumentType documentType,
            String extractedText
    );
}