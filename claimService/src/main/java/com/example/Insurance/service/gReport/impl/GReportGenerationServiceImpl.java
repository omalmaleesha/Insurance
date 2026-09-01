package com.example.Insurance.service.gReport.impl;

import com.example.Insurance.dto.StructuredClaimDocument;
import com.example.Insurance.entities.Claim;
import com.example.Insurance.entities.customer.CorporateCustomer;
import com.example.Insurance.entities.customer.Customer;
import com.example.Insurance.entities.customer.PersonalCustomer;
import com.example.Insurance.entities.policy.Policy;
import com.example.Insurance.repository.CustomerRepository;
import com.example.Insurance.repository.PolicyRepository;
import com.example.Insurance.service.gReport.GReportDataService;
import com.example.Insurance.service.gReport.GReportDocumentService;
import com.example.Insurance.service.gReport.GReportGenerationService;
import com.example.Insurance.service.gReport.GReportStructuredExtractionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GReportGenerationServiceImpl implements GReportGenerationService {

    private final GReportDataService gReportDataService;
    private final GReportDocumentService gReportDocumentService;
    private final GReportStructuredExtractionService gReportStructuredExtractionService;
    private final CustomerRepository customerRepository;
    private final PolicyRepository policyRepository;


    @Override
    public void generate(Long jobId) {

        log.info(
                "=================================================="
        );

        log.info(
                "Starting G-report generation. jobId={}",
                jobId
        );

        /*
         * ==================================================
         * 1. LOAD CLAIM + REQUIRED DOCUMENTS
         * ==================================================
         */

        GReportProcessingContext context =
                gReportDataService.loadContext(jobId);

        Claim claim = context.claim();

        log.info(
                "G-report context loaded. jobId={}, claimId={}, claimNumber={}",
                jobId,
                claim.getId(),
                claim.getClaimNumber()
        );


        /*
         * ==================================================
         * 2. EXTRACT TEXT FROM CLAIM FORM
         * ==================================================
         */

        String claimFormText =
                gReportDocumentService.extractText(
                        context.claimForm()
                );


        /*
         * ==================================================
         * 3. EXTRACT TEXT FROM CUSTOMER / T.O. STATEMENT
         * ==================================================
         */

        String customerStatementText =
                gReportDocumentService.extractText(
                        context.customerStatement()
                );


        log.info(
                "PDF extraction completed. jobId={}, claimFormChars={}, customerStatementChars={}",
                jobId,
                claimFormText.length(),
                customerStatementText.length()
        );


        /*
         * ==================================================
         * 4. STRUCTURED EXTRACTION
         *
         * No AI here.
         *
         * GReportStructuredExtractionService is responsible
         * for converting PDF text into structured fields.
         * ==================================================
         */

        StructuredClaimDocument claimForm = gReportStructuredExtractionService.extract(claimFormText);

        StructuredClaimDocument customerStatement =
                gReportStructuredExtractionService.extract(
                        customerStatementText
                );


        log.info(
                "Structured extraction completed. claimNumber={}, policyNumber={}",
                claimForm.claimNumber(),
                claimForm.policyNumber()
        );


        /*
         * ==================================================
         * 5. BASIC DOCUMENT CONSISTENCY CHECK
         *
         * Check whether both documents belong to the same
         * claim before touching customer/policy processing.
         * ==================================================
         */

        List<String> documentIssues =
                new ArrayList<>();


        compareValue(
                "Claim Number",
                claimForm.claimNumber(),
                customerStatement.claimNumber(),
                documentIssues
        );

        compareValue(
                "Policy Number",
                claimForm.policyNumber(),
                customerStatement.policyNumber(),
                documentIssues
        );

        compareValue(
                "Customer Name",
                claimForm.customerName(),
                customerStatement.customerName(),
                documentIssues
        );

        compareValue(
                "Premises Address",
                claimForm.premisesAddress(),
                customerStatement.premisesAddress(),
                documentIssues
        );

        compareValue(
                "Incident Date",
                claimForm.incidentDate(),
                customerStatement.incidentDate(),
                documentIssues
        );


        if (!documentIssues.isEmpty()) {

            log.warn(
                    "Document consistency issues detected. jobId={}, issues={}",
                    jobId,
                    documentIssues
            );

        } else {

            log.info(
                    "Claim Form and Customer Statement are consistent. jobId={}",
                    jobId
            );
        }


        /*
         * ==================================================
         * 6. LOAD CUSTOMER FROM DATABASE
         * ==================================================
         */

        Customer customer =
                customerRepository
                        .findById(
                                claim.getCustomerId()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Customer not found. customerId="
                                                + claim.getCustomerId()
                                )
                        );


        log.info(
                "Customer loaded. customerId={}, customerCode={}, type={}",
                customer.getId(),
                customer.getCustomerCode(),
                customer.getCustomerType()
        );


        /*
         * ==================================================
         * 7. LOAD POLICY
         *
         * IMPORTANT:
         *
         * We verify BOTH:
         *
         * policy number
         * +
         * customer ID
         *
         * This prevents accidentally matching a policy that
         * belongs to another customer.
         * ==================================================
         */

        if (claimForm.policyNumber() == null
                || claimForm.policyNumber().isBlank()) {

            throw new RuntimeException(
                    "Policy number could not be extracted from claim form"
            );
        }


        Policy policy =
                policyRepository
                        .findByPolicyNumberAndCustomerId(
                                claimForm.policyNumber(),
                                customer.getId()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Policy not found or policy does not belong "
                                                + "to customer. policyNumber="
                                                + claimForm.policyNumber()
                                                + ", customerId="
                                                + customer.getId()
                                )
                        );


        log.info(
                "Policy loaded. policyNumber={}, policyType={}, status={}",
                policy.getPolicyNumber(),
                policy.getPolicyType(),
                policy.getStatus()
        );


        /*
         * ==================================================
         * 8. DATABASE VS DOCUMENT COMPARISON
         * ==================================================
         */

        List<String> databaseIssues =
                new ArrayList<>();


        /*
         * CLAIM NUMBER
         */

        compareValue(
                "Claim Number",
                claim.getClaimNumber(),
                claimForm.claimNumber(),
                databaseIssues
        );


        /*
         * POLICY NUMBER
         */

        compareValue(
                "Policy Number",
                policy.getPolicyNumber(),
                claimForm.policyNumber(),
                databaseIssues
        );


        /*
         * CUSTOMER CODE
         */

        compareValue(
                "Customer Code",
                customer.getCustomerCode(),
                claimForm.customerCode(),
                databaseIssues
        );


        /*
         * CUSTOMER NAME
         */

        String databaseCustomerName =
                getCustomerName(customer);

        compareValue(
                "Customer Name",
                databaseCustomerName,
                claimForm.customerName(),
                databaseIssues
        );
        compareValue(
                "Customer Address",
                customer.getAddress(),
                claimForm.premisesAddress(),
                databaseIssues
        );

        compareValue(
                "Insured Name",
                policy.getInsuredName(),
                claimForm.customerName(),
                databaseIssues
        );

        compareValue(
                "Insured NIC",
                policy.getInsuredNic(),
                claimForm.nic(),
                databaseIssues
        );

        compareValue(
                "Risk Location",
                policy.getRiskLocation(),
                claimForm.premisesAddress(),
                databaseIssues
        );

        compareValue(
                "Incident Date",
                claim.getIncidentDate(),
                claimForm.incidentDate(),
                databaseIssues
        );


        boolean documentsValid =
                documentIssues.isEmpty();

        boolean databaseValid =
                databaseIssues.isEmpty();


        boolean overallValid =
                documentsValid && databaseValid;


        if (overallValid) {

            log.info(
                    "G-REPORT VALIDATION PASSED. jobId={}, claimId={}",
                    jobId,
                    claim.getId()
            );

        } else {

            log.warn(
                    "G-REPORT VALIDATION FAILED. jobId={}, claimId={}, documentIssues={}, databaseIssues={}",
                    jobId,
                    claim.getId(),
                    documentIssues,
                    databaseIssues
            );
        }

        String gReport =
                buildGReport(
                        claim,
                        customer,
                        policy,
                        claimForm,
                        customerStatement,
                        documentIssues,
                        databaseIssues,
                        overallValid
                );


        log.info(
                "G-report content generated. jobId={}, characters={}",
                jobId,
                gReport.length()
        );

        log.info(
                "\n========== G-REPORT ==========\n{}\n========== END G-REPORT ==========",
                gReport
        );


        log.info(
                "G-report generation completed. jobId={}, claimId={}",
                jobId,
                claim.getId()
        );

        log.info(
                "=================================================="
        );
    }

    private String getCustomerName(
            Customer customer
    ) {

        if (customer instanceof PersonalCustomer personalCustomer) {

            String firstName =
                    safe(personalCustomer.getFirstName());

            String lastName =
                    safe(personalCustomer.getLastName());

            return normalize(
                    firstName + " " + lastName
            );
        }


        if (customer instanceof CorporateCustomer corporateCustomer) {

            return safe(
                    corporateCustomer.getCompanyName()
            );
        }


        return "";
    }

    private void compareValue(
            String field,
            Object databaseValue,
            Object documentValue,
            List<String> issues
    ) {

        if (databaseValue == null
                || documentValue == null) {

            if (databaseValue == null
                    && documentValue == null) {

                return;
            }

            issues.add(
                    field
                            + " mismatch. database="
                            + databaseValue
                            + ", document="
                            + documentValue
            );

            return;
        }


        String database =
                normalize(
                        String.valueOf(databaseValue)
                );

        String document =
                normalize(
                        String.valueOf(documentValue)
                );


        if (database.isBlank()
                || document.isBlank()) {

            return;
        }


        if (!database.equalsIgnoreCase(document)) {

            issues.add(
                    field
                            + " mismatch. database="
                            + databaseValue
                            + ", document="
                            + documentValue
            );

            log.warn(
                    "{} mismatch. database={}, document={}",
                    field,
                    databaseValue,
                    documentValue
            );

        } else {

            log.debug(
                    "{} matched. value={}",
                    field,
                    databaseValue
            );
        }
    }

    private String normalize(
            String value
    ) {

        if (value == null) {
            return "";
        }

        return value
                .trim()
                .replaceAll("\\s+", " ")
                .replaceAll("[,.]", "")
                .toLowerCase();
    }


    private String safe(
            String value
    ) {

        return value == null
                ? ""
                : value.trim();
    }

    private String buildGReport(

            Claim claim,

            Customer customer,

            Policy policy,

            StructuredClaimDocument claimForm,

            StructuredClaimDocument customerStatement,

            List<String> documentIssues,

            List<String> databaseIssues,

            boolean overallValid

    ) {

        StringBuilder report =
                new StringBuilder();


        report.append(
                "G-REPORT\n"
        );

        report.append(
                "========================================\n"
        );


        report.append(
                "Claim Information\n"
        );

        report.append(
                "----------------------------------------\n"
        );

        report.append(
                "Claim Number: "
        ).append(
                claim.getClaimNumber()
        ).append("\n");


        report.append(
                "Claim ID: "
        ).append(
                claim.getId()
        ).append("\n");


        report.append(
                "Claim Type: "
        ).append(
                claim.getClaimType()
        ).append("\n");


        report.append(
                "Claim Status: "
        ).append(
                claim.getStatus()
        ).append("\n");


        report.append(
                "Incident Date: "
        ).append(
                claim.getIncidentDate()
        ).append("\n");


        report.append(
                "Reported Date: "
        ).append(
                claim.getReportedDate()
        ).append("\n");


        report.append(
                "Incident Location: "
        ).append(
                claim.getIncidentLocation()
        ).append("\n");


        report.append("\n");


        /*
         * CUSTOMER
         */

        report.append(
                "Customer Information\n"
        );

        report.append(
                "----------------------------------------\n"
        );


        report.append(
                "Customer ID: "
        ).append(
                customer.getId()
        ).append("\n");


        report.append(
                "Customer Code: "
        ).append(
                customer.getCustomerCode()
        ).append("\n");


        report.append(
                "Customer Name: "
        ).append(
                getCustomerName(customer)
        ).append("\n");


        report.append(
                "Customer Type: "
        ).append(
                customer.getCustomerType()
        ).append("\n");


        report.append(
                "Email: "
        ).append(
                customer.getEmail()
        ).append("\n");


        report.append(
                "Phone: "
        ).append(
                customer.getPhoneNumber()
        ).append("\n");


        report.append(
                "Address: "
        ).append(
                customer.getAddress()
        ).append("\n");


        report.append("\n");


        /*
         * POLICY
         */

        report.append(
                "Policy Information\n"
        );

        report.append(
                "----------------------------------------\n"
        );


        report.append(
                "Policy Number: "
        ).append(
                policy.getPolicyNumber()
        ).append("\n");


        report.append(
                "Policy Type: "
        ).append(
                policy.getPolicyType()
        ).append("\n");


        report.append(
                "Product Name: "
        ).append(
                policy.getProductName()
        ).append("\n");


        report.append(
                "Policy Status: "
        ).append(
                policy.getStatus()
        ).append("\n");


        report.append(
                "Inception Date: "
        ).append(
                policy.getInceptionDate()
        ).append("\n");


        report.append(
                "Expiry Date: "
        ).append(
                policy.getExpiryDate()
        ).append("\n");


        report.append(
                "Risk Location: "
        ).append(
                policy.getRiskLocation()
        ).append("\n");


        report.append(
                "Total Sum Insured: "
        ).append(
                policy.getTotalSumInsured()
        ).append("\n");


        report.append(
                "Deductible: "
        ).append(
                policy.getDeductible()
        ).append("\n");


        report.append("\n");


        /*
         * DOCUMENT INFORMATION
         */

        report.append(
                "Document Information\n"
        );

        report.append(
                "----------------------------------------\n"
        );


        report.append(
                "Claim Form Claim Number: "
        ).append(
                claimForm.claimNumber()
        ).append("\n");


        report.append(
                "Claim Form Policy Number: "
        ).append(
                claimForm.policyNumber()
        ).append("\n");


        report.append(
                "Claim Form Customer: "
        ).append(
                claimForm.customerName()
        ).append("\n");


        report.append(
                "Claim Form Premises: "
        ).append(
                claimForm.premisesAddress()
        ).append("\n");


        report.append(
                "Customer Statement Claim Number: "
        ).append(
                customerStatement.claimNumber()
        ).append("\n");


        report.append(
                "Customer Statement Policy Number: "
        ).append(
                customerStatement.policyNumber()
        ).append("\n");


        report.append("\n");


        /*
         * VALIDATION
         */

        report.append(
                "Validation Result\n"
        );

        report.append(
                "----------------------------------------\n"
        );


        report.append(
                "Overall Result: "
        ).append(
                overallValid
                        ? "PASSED"
                        : "FAILED"
        ).append("\n");


        /*
         * DOCUMENT ISSUES
         */

        report.append(
                "\nDocument Comparison\n"
        );


        if (documentIssues.isEmpty()) {

            report.append(
                    "No document inconsistencies detected.\n"
            );

        } else {

            for (String issue : documentIssues) {

                report.append(
                        " - "
                ).append(
                        issue
                ).append("\n");
            }
        }


        /*
         * DATABASE ISSUES
         */

        report.append(
                "\nDatabase Comparison\n"
        );


        if (databaseIssues.isEmpty()) {

            report.append(
                    "No database inconsistencies detected.\n"
            );

        } else {

            for (String issue : databaseIssues) {

                report.append(
                        " - "
                ).append(
                        issue
                ).append("\n");
            }
        }


        report.append(
                "\n========================================\n"
        );

        report.append(
                "End of G-report\n"
        );


        return report.toString();
    }
}