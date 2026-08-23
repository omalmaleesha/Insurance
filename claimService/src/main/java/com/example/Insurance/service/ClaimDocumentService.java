package com.example.Insurance.service;


import com.example.Insurance.dto.ClaimDocumentDTO;
import com.example.Insurance.utils.types.ClaimDocumentType;
import com.example.Insurance.utils.types.DocumentSource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ClaimDocumentService {

    ClaimDocumentDTO uploadDocument(
            Long claimId,
            MultipartFile file,
            ClaimDocumentType documentType,
            DocumentSource documentSource,
            String uploadedByEtfNo
    );

    List<ClaimDocumentDTO> getDocumentsByClaimId(Long claimId);

    ClaimDocumentDTO getDocumentById(Long documentId);

    ClaimDocumentDTO verifyDocument(Long documentId);

    ClaimDocumentDTO rejectDocument(
            Long documentId,
            String reason
    );

    void deleteDocument(Long documentId);
}