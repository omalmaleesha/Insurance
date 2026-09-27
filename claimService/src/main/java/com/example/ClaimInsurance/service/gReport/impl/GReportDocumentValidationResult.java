package com.example.ClaimInsurance.service.gReport.impl;

public record GReportDocumentValidationResult(
        boolean valid,
        String reason
) {

    public static GReportDocumentValidationResult success() {
        return new GReportDocumentValidationResult(
                true,
                null
        );
    }

    public static GReportDocumentValidationResult invalid(
            String reason
    ) {
        return new GReportDocumentValidationResult(
                false,
                reason
        );
    }
}