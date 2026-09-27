package com.example.ClaimInsurance.service.gReport.impl;

import com.example.ClaimInsurance.service.gReport.GReportDocumentValidationService;
import com.example.ClaimInsurance.utils.types.ClaimDocumentType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Locale;

@Service
@Slf4j
public class GReportDocumentValidationServiceImpl implements GReportDocumentValidationService {
    @Override
    public GReportDocumentValidationResult validate(ClaimDocumentType documentType, String extractedText) {
        if (extractedText == null ||
                extractedText.isBlank()) {

            return GReportDocumentValidationResult.invalid(
                    "Document contains no readable text."
            );
        }

        String normalizedText =
                extractedText
                        .toLowerCase(Locale.ROOT);

        return switch (documentType) {

            case CLAIM_FORM ->
                    validateClaimForm(normalizedText);

            case CUSTOMER_STATEMENT ->
                    validateCustomerStatement(normalizedText);

            default ->
                    GReportDocumentValidationResult.invalid(
                            "Document type is not required for G-report generation."
                    );
        };
    }

    private GReportDocumentValidationResult validateClaimForm(String text) {
        List<String> indicators =
                List.of(
                        "claim",
                        "policy",
                        "incident",
                        "customer"
                );
        return validateIndicators(
                text,
                indicators,
                "Document does not appear to be a claim form."
        );
    }

    private GReportDocumentValidationResult validateCustomerStatement(String text) {
        List<String> indicators =
                List.of(
                        "statement",
                        "customer",
                        "incident",
                        "claim"
                );
        return validateIndicators(
                text,
                indicators,
                "Document does not appear to be a customer statement."
        );
    }

    private GReportDocumentValidationResult validateIndicators(String text, List<String> indicators, String failureReason) {
        int matchedIndicators = 0;
        for (String indicator : indicators) {

            if (text.contains(indicator)) {
                matchedIndicators++;
            }
        }
        /*
         * Require at least 2 indicators.
         *
         * This is intentionally cheap and conservative.
         * It is not the final document verification mechanism.
         */
        if (matchedIndicators < 2) {
            log.warn(
                    "Document failed basic type validation. matchedIndicators={}",
                    matchedIndicators
            );
            return GReportDocumentValidationResult.invalid(
                    failureReason
            );
        }
        return GReportDocumentValidationResult.success();
    }
}