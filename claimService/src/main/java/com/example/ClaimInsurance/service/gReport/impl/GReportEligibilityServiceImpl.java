package com.example.ClaimInsurance.service.gReport.impl;

import com.example.ClaimInsurance.repository.ClaimDocumentRepository;
import com.example.ClaimInsurance.repository.ClaimRepository;
import com.example.ClaimInsurance.service.gReport.GReportEligibilityService;
import com.example.ClaimInsurance.utils.types.ClaimDocumentType;
import com.example.ClaimInsurance.utils.types.DocumentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GReportEligibilityServiceImpl implements GReportEligibilityService {

    private final ClaimRepository claimRepository;
    private final ClaimDocumentRepository claimDocumentRepository;

    @Override
    @Transactional(readOnly = true)
    public boolean isEligible(Long claimId) {
        if (!claimRepository.existsById(claimId)) {
            return false;
        }
        boolean hasClaimForm =
                claimDocumentRepository
                        .existsByClaimIdAndDocumentTypeAndStatus(
                                claimId,
                                ClaimDocumentType.CLAIM_FORM,
                                DocumentStatus.VERIFIED
                        );

        if (!hasClaimForm) {
            return false;
        }
        return claimDocumentRepository
                .existsByClaimIdAndDocumentTypeAndStatus(
                        claimId,
                        ClaimDocumentType.CUSTOMER_STATEMENT,
                        DocumentStatus.VERIFIED
                );
    }
}
