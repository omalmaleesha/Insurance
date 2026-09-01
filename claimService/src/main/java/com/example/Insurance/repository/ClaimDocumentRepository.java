package com.example.Insurance.repository;

import com.example.Insurance.entities.ClaimDocument;
import com.example.Insurance.utils.types.ClaimDocumentType;
import com.example.Insurance.utils.types.DocumentStatus;

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