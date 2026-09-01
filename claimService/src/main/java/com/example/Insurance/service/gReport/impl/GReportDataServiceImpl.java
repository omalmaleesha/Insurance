package com.example.Insurance.service.gReport.impl;


import com.example.Insurance.entities.Claim;
import com.example.Insurance.entities.ClaimDocument;
import com.example.Insurance.entities.GReportGenerationJob;
import com.example.Insurance.entities.customer.Customer;
import com.example.Insurance.entities.policy.Policy;
import com.example.Insurance.repository.ClaimDocumentRepository;
import com.example.Insurance.repository.CustomerRepository;
import com.example.Insurance.repository.GReportGenerationJobRepository;
import com.example.Insurance.repository.PolicyRepository;
import com.example.Insurance.service.gReport.GReportDataService;
import com.example.Insurance.utils.types.ClaimDocumentType;
import com.example.Insurance.utils.types.DocumentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GReportDataServiceImpl implements GReportDataService {

    private final GReportGenerationJobRepository jobRepository;
    private final ClaimDocumentRepository claimDocumentRepository;
    private final CustomerRepository customerRepository;
    private final PolicyRepository policyRepository;

    @Override
    @Transactional(readOnly = true)
    public GReportProcessingContext loadContext(Long jobId) {

        GReportGenerationJob job =
                jobRepository.findById(jobId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "G-report job not found: " + jobId
                                )
                        );

        Claim claim = job.getClaim();

        if (claim == null) {
            throw new RuntimeException(
                    "Claim not found for G-report job: " + jobId
            );
        }

        Customer customer =
                customerRepository.findById(claim.getCustomerId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Customer not found: "
                                                + claim.getCustomerId()
                                )
                        );

        List<Policy> policies =
                policyRepository.findByCustomerId(
                        claim.getCustomerId()
                );

        ClaimDocument claimForm =
                claimDocumentRepository
                        .findByClaimIdAndDocumentTypeAndStatus(
                                claim.getId(),
                                ClaimDocumentType.CLAIM_FORM,
                                DocumentStatus.VERIFIED
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Verified claim form not found for claim: "
                                                + claim.getId()
                                )
                        );

        ClaimDocument customerStatement =
                claimDocumentRepository
                        .findByClaimIdAndDocumentTypeAndStatus(
                                claim.getId(),
                                ClaimDocumentType.CUSTOMER_STATEMENT,
                                DocumentStatus.VERIFIED
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Verified customer statement not found for claim: "
                                                + claim.getId()
                                )
                        );

        return new GReportProcessingContext(
                claim,
                claimForm,
                customerStatement,
                customer,
                policies
        );
    }

    @Override
    @Transactional(readOnly = true)
    public GReportDatabaseContext loadDatabaseContext(
            Long jobId
    ) {

        GReportGenerationJob job =
                jobRepository.findById(jobId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "G-report job not found: " + jobId
                                )
                        );

        Claim claim = job.getClaim();

        if (claim == null) {
            throw new RuntimeException(
                    "Claim not found for job: " + jobId
            );
        }

        Customer customer =
                customerRepository
                        .findById(claim.getCustomerId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Customer not found: "
                                                + claim.getCustomerId()
                                )
                        );

        Policy policy =
                policyRepository
                        .findByPolicyNumberAndCustomerId(
                                claim.getPolicyNumber(),
                                customer.getId()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Policy not found for claim/customer"
                                )
                        );

        return new GReportDatabaseContext(
                claim,
                customer,
                policy
        );
    }
}