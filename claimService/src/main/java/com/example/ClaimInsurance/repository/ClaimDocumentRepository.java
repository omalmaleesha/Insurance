package com.example.ClaimInsurance.repository;

import com.example.ClaimInsurance.entities.ClaimDocument;
import com.example.ClaimInsurance.utils.types.ClaimDocumentType;
import com.example.ClaimInsurance.utils.types.DocumentStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClaimDocumentRepository extends JpaRepository<ClaimDocument, Long> {

    List<ClaimDocument> findByClaimId(Long claimId);

    Optional<ClaimDocument> findByClaimIdAndDocumentTypeAndStatus(
            Long claimId,
            ClaimDocumentType documentType,
            DocumentStatus status
    );

    boolean existsByClaimIdAndDocumentTypeAndStatus(
            Long claimId,
            ClaimDocumentType documentType,
            DocumentStatus status
    );

    boolean existsByFileHash(String fileHash);
}