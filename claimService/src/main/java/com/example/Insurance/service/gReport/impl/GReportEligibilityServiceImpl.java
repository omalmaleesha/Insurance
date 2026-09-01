package com.example.Insurance.service.gReport.impl;

import com.example.Insurance.repository.ClaimDocumentRepository;
import com.example.Insurance.repository.ClaimRepository;
import com.example.Insurance.service.gReport.GReportEligibilityService;
import com.example.Insurance.utils.types.ClaimDocumentType;
import com.example.Insurance.utils.types.DocumentStatus;
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
