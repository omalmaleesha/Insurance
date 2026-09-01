package com.example.Insurance.service.gReport.impl;

import com.example.Insurance.dto.StructuredClaimDocument;
import com.example.Insurance.service.gReport.GReportStructuredExtractionService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class GReportStructuredExtractionServiceImpl
        implements GReportStructuredExtractionService {

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("d MMMM yyyy");

    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern("hh:mm a");

    @Override
    public StructuredClaimDocument extract(String documentText) {

        if (documentText == null || documentText.isBlank()) {
            throw new IllegalArgumentException(
                    "Document text cannot be empty"
            );
        }

        return new StructuredClaimDocument(

                extractField(documentText, "Claim Number"),

                extractField(documentText, "Policy Number"),

                firstNonBlank(
                        extractField(
                                documentText,
                                "Customer / Insured Name"
                        ),
                        extractField(
                                documentText,
                                "Customer / Insured"
                        )
                ),

                extractField(
                        documentText,
                        "Customer Code"
                ),

                extractField(
                        documentText,
                        "NIC"
                ),

                extractField(
                        documentText,
                        "Business Name"
                ),

                extractField(
                        documentText,
                        "Business Registration No."
                ),

                firstNonBlank(
                        extractField(
                                documentText,
                                "Risk Location"
                        ),
                        extractField(
                                documentText,
                                "Premises Address"
                        )
                ),

                parseDate(
                        extractField(
                                documentText,
                                "Date of Incident"
                        )
                ),

                parseTime(
                        extractField(
                                documentText,
                                "Approximate Time"
                        )
                ),

                extractField(
                        documentText,
                        "Incident Location"
                ),

                extractField(
                        documentText,
                        "Cause / Nature of Loss"
                ),

                extractMultilineField(
                        documentText,
                        "Incident Description"
                ),

                extractMultilineField(
                        documentText,
                        "Building Damage"
                ),

                extractMultilineField(
                        documentText,
                        "Contents / Stock Damage"
                ),

                parseMoney(
                        extractField(
                                documentText,
                                "Estimated Building Loss"
                        )
                ),

                parseMoney(
                        extractField(
                                documentText,
                                "Estimated Contents Loss"
                        )
                ),

                parseMoney(
                        extractField(
                                documentText,
                                "Total Estimated Loss"
                        )
                ),

                firstNonBlank(
                        extractField(
                                documentText,
                                "Fire Authority"
                        ),
                        extractAuthorityName(documentText)
                ),

                extractFireReference(documentText),

                extractField(
                        documentText,
                        "Other Insurance Cover"
                ),

                extractField(
                        documentText,
                        "Previous Claim for Same Premises"
                )
        );
    }

    private String extractField(
            String text,
            String field
    ) {

        Pattern pattern = Pattern.compile(
                "(?m)^\\s*"
                        + Pattern.quote(field)
                        + "\\s+(.+?)\\s*$"
        );

        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            return clean(matcher.group(1));
        }

        return null;
    }

    private String extractMultilineField(
            String text,
            String field
    ) {

        String[] lines = text.split("\\R");

        StringBuilder result =
                new StringBuilder();

        boolean found = false;

        for (String rawLine : lines) {

            String line = rawLine.trim();

            if (line.equalsIgnoreCase(field)) {
                found = true;
                continue;
            }

            if (!found) {
                continue;
            }

            if (line.isBlank()) {

                if (!result.isEmpty()) {
                    break;
                }

                continue;
            }

            if (isFieldLabel(line)) {
                break;
            }

            if (!result.isEmpty()) {
                result.append(" ");
            }

            result.append(line);
        }

        return clean(
                result.isEmpty()
                        ? null
                        : result.toString()
        );
    }

    private boolean isFieldLabel(String line) {

        String[] fields = {

                "Claim Number",
                "Policy Number",
                "Policy Type",
                "Date Claim Reported",
                "Branch",

                "Customer / Insured Name",
                "Customer / Insured",
                "Customer Code",
                "NIC",
                "Business Name",
                "Business Registration No.",
                "Address",
                "Telephone",
                "Email",
                "Occupation / Position",

                "Risk Location",
                "Premises Use",
                "Building Type",
                "Year Built",
                "Insured Interest",

                "Date of Incident",
                "Approximate Time",
                "Incident Location",
                "Cause / Nature of Loss",
                "Incident Description",
                "Emergency Measures",
                "Injuries",
                "Police / Fire Authority",

                "Building Damage",
                "Contents / Stock Damage",
                "Estimated Building Loss",
                "Estimated Contents Loss",
                "Total Estimated Loss",

                "Other Insurance Cover",
                "Previous Claim for Same Premises"
        };

        for (String field : fields) {

            if (line.equalsIgnoreCase(field)) {
                return true;
            }
        }

        return false;
    }

    private LocalDate parseDate(String value) {

        if (value == null || value.isBlank()) {
            return null;
        }

        try {

            return LocalDate.parse(
                    value.trim(),
                    DATE_FORMAT
            );

        } catch (Exception e) {

            throw new IllegalArgumentException(
                    "Unable to parse incident date: "
                            + value,
                    e
            );
        }
    }

    private LocalTime parseTime(String value) {

        if (value == null || value.isBlank()) {
            return null;
        }

        try {

            return LocalTime.parse(
                    value.trim(),
                    TIME_FORMAT
            );

        } catch (Exception e) {

            throw new IllegalArgumentException(
                    "Unable to parse incident time: "
                            + value,
                    e
            );
        }
    }

    private BigDecimal parseMoney(String value) {

        if (value == null || value.isBlank()) {
            return null;
        }

        String cleaned =
                value
                        .replace("LKR", "")
                        .replace(",", "")
                        .trim();

        try {

            return new BigDecimal(cleaned);

        } catch (Exception e) {

            throw new IllegalArgumentException(
                    "Unable to parse monetary value: "
                            + value,
                    e
            );
        }
    }

    private String extractAuthorityName(String text) {

        String value =
                extractField(
                        text,
                        "Police / Fire Authority"
                );

        if (value == null) {
            return null;
        }

        int index =
                value.toLowerCase()
                        .indexOf("attended");

        if (index > 0) {
            return value.substring(0, index).trim();
        }

        return value;
    }

    private String extractFireReference(String text) {

        String value =
                extractField(
                        text,
                        "Police / Fire Authority"
                );

        if (value == null) {
            return null;
        }

        Pattern pattern =
                Pattern.compile(
                        "reference:\\s*([A-Za-z0-9/_-]+)",
                        Pattern.CASE_INSENSITIVE
                );

        Matcher matcher =
                pattern.matcher(value);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return null;
    }

    private String firstNonBlank(
            String first,
            String second
    ) {

        if (first != null && !first.isBlank()) {
            return first;
        }

        return second;
    }

    private String clean(String value) {

        if (value == null) {
            return null;
        }

        return value
                .replaceAll("\\s+", " ")
                .trim();
    }
}