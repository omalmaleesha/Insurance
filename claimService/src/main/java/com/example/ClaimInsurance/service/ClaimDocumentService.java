package com.example.ClaimInsurance.service;

import com.example.ClaimInsurance.dto.ClaimDocumentDTO;

import com.example.ClaimInsurance.entities.GReportGenerationJob;
import com.example.ClaimInsurance.service.storage.StorageFile;

import com.example.ClaimInsurance.utils.types.ClaimDocumentType;
import com.example.ClaimInsurance.utils.types.DocumentSource;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface ClaimDocumentService {

    GReportGenerationJob getGReportFile(String claimId);

    ClaimDocumentDTO uploadDocument(
            Long claimId,
            MultipartFile file,
            ClaimDocumentType documentType,
            DocumentSource documentSource,
            String uploadedByEtfNo
    );

    List<ClaimDocumentDTO> getDocumentsByClaimId(
            Long claimId
    );

    ClaimDocumentDTO getDocumentById(
            Long documentId
    );

    ClaimDocumentDTO verifyDocument(
            Long documentId
    );

    ClaimDocumentDTO rejectDocument(
            Long documentId,
            String reason
    );

    void deleteDocument(
            Long documentId
    );

    StorageFile getDocumentFile(
            Long documentId
    );
}