package com.example.Insurance.service.impl;
import com.example.Insurance.config.GoogleDriveProperties;
import com.example.Insurance.dto.ClaimDocumentDTO;
import com.example.Insurance.entities.Claim;
import com.example.Insurance.entities.ClaimDocument;
import com.example.Insurance.repository.ClaimDocumentRepository;
import com.example.Insurance.repository.ClaimRepository;
import com.example.Insurance.service.ClaimDocumentService;
import com.example.Insurance.service.storage.FileStorageService;
import com.example.Insurance.service.storage.StoredFile;
import com.example.Insurance.service.storage.StorageFile;
import com.example.Insurance.utils.types.ClaimDocumentType;
import com.example.Insurance.utils.types.DocumentSource;
import com.example.Insurance.utils.types.DocumentStatus;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class ClaimDocumentServiceImpl
        implements ClaimDocumentService {


    // =========================================================
    // CONSTANTS
    // =========================================================

    private static final long MAX_FILE_SIZE =
            10 * 1024 * 1024;

    private static final Set<String> ALLOWED_CONTENT_TYPES =
            Set.of(

                    "application/pdf",

                    "image/jpeg",

                    "image/png"

            );


    // =========================================================
    // DEPENDENCIES
    // =========================================================

    private final ClaimDocumentRepository claimDocumentRepository;

    private final ClaimRepository claimRepository;

    private final FileStorageService fileStorageService;
    private final GoogleDriveProperties googleDriveProperties;


    // =========================================================
    // UPLOAD DOCUMENT
    // =========================================================

    @Override
    public ClaimDocumentDTO uploadDocument(

            Long claimId,

            MultipartFile file,

            ClaimDocumentType documentType,

            DocumentSource documentSource,

            String uploadedByEtfNo
    ) {

        validateClaimId(claimId);

        validateUploadRequest(

                file,

                documentType,

                documentSource,

                uploadedByEtfNo

        );


        Claim claim =
                claimRepository
                        .findById(claimId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Claim not found with ID: "
                                                + claimId
                                )
                        );


        StoredFile storedFile = null;


        try {

            // -------------------------------------------------
            // 1. UPLOAD TO CLOUD STORAGE
            // -------------------------------------------------

            storedFile =
                    fileStorageService.upload(

                            file,

                            getClaimFolderId(claim)

                    );


            // -------------------------------------------------
            // 2. CREATE DATABASE ENTITY
            // -------------------------------------------------

            ClaimDocument document =
                    ClaimDocument.builder()

                            .claim(claim)

                            .documentType(
                                    documentType
                            )

                            .documentSource(
                                    documentSource
                            )

                            .status(
                                    DocumentStatus.UPLOADED
                            )

                            .fileName(
                                    sanitizeFileName(
                                            file.getOriginalFilename()
                                    )
                            )

                            .storageProvider(
                                    storedFile.provider()
                            )

                            .storageFileId(
                                    storedFile.fileId()
                            )

                            .storageFolderId(
                                    storedFile.folderId()
                            )

                            .contentType(
                                    storedFile.contentType()
                            )

                            .fileSize(
                                    storedFile.fileSize()
                            )

                            .uploadedByEtfNo(
                                    uploadedByEtfNo.trim()
                            )

                            .build();


            // -------------------------------------------------
            // 3. SAVE DOCUMENT METADATA
            // -------------------------------------------------

            ClaimDocument savedDocument =
                    claimDocumentRepository.save(
                            document
                    );


            return mapToDTO(
                    savedDocument
            );

        } catch (Exception e) {

            // -------------------------------------------------
            // COMPENSATING ACTION
            //
            // File uploaded successfully
            // but database save failed.
            //
            // Try to remove orphan cloud file.
            // -------------------------------------------------

            if (storedFile != null) {

                try {

                    fileStorageService.delete(
                            storedFile.fileId()
                    );

                } catch (Exception cleanupException) {

                    //
                    // Add proper logging / retry mechanism.
                    //
                    // Do not hide original exception.
                }
            }

            throw e;
        }
    }


    // =========================================================
    // GET DOCUMENTS BY CLAIM ID
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<ClaimDocumentDTO> getDocumentsByClaimId(
            Long claimId
    ) {

        validateClaimId(claimId);


        if (!claimRepository.existsById(claimId)) {

            throw new RuntimeException(
                    "Claim not found with ID: "
                            + claimId
            );
        }


        return claimDocumentRepository
                .findByClaimId(claimId)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }


    // =========================================================
    // GET DOCUMENT BY ID
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public ClaimDocumentDTO getDocumentById(
            Long documentId
    ) {

        validateDocumentId(documentId);


        ClaimDocument document =
                findDocumentById(
                        documentId
                );


        return mapToDTO(
                document
        );
    }


    // =========================================================
    // VERIFY DOCUMENT
    // =========================================================

    @Override
    public ClaimDocumentDTO verifyDocument(
            Long documentId
    ) {

        validateDocumentId(documentId);


        ClaimDocument document =
                findDocumentById(
                        documentId
                );


        if (document.getStatus()
                == DocumentStatus.REJECTED) {

            throw new IllegalStateException(
                    "Rejected document cannot be verified"
            );
        }


        if (document.getStatus()
                == DocumentStatus.VERIFIED) {

            throw new IllegalStateException(
                    "Document is already verified"
            );
        }


        document.setStatus(
                DocumentStatus.VERIFIED
        );


        document.setRejectionReason(
                null
        );


        ClaimDocument updatedDocument =
                claimDocumentRepository.save(
                        document
                );


        return mapToDTO(
                updatedDocument
        );
    }


    // =========================================================
    // REJECT DOCUMENT
    // =========================================================

    @Override
    public ClaimDocumentDTO rejectDocument(

            Long documentId,

            String reason
    ) {

        validateDocumentId(
                documentId
        );


        if (reason == null
                || reason.isBlank()) {

            throw new IllegalArgumentException(
                    "Rejection reason is required"
            );
        }


        ClaimDocument document =
                findDocumentById(
                        documentId
                );


        if (document.getStatus()
                == DocumentStatus.VERIFIED) {

            throw new IllegalStateException(
                    "Verified document cannot be rejected"
            );
        }


        document.setStatus(
                DocumentStatus.REJECTED
        );


        document.setRejectionReason(
                reason.trim()
        );


        ClaimDocument updatedDocument =
                claimDocumentRepository.save(
                        document
                );


        return mapToDTO(
                updatedDocument
        );
    }


    // =========================================================
    // DELETE DOCUMENT
    // =========================================================

    @Override
    public void deleteDocument(
            Long documentId
    ) {

        validateDocumentId(
                documentId
        );


        ClaimDocument document =
                findDocumentById(
                        documentId
                );


        // -------------------------------------------------
        // DELETE FILE FROM CLOUD STORAGE
        // -------------------------------------------------

        fileStorageService.delete(
                document.getStorageFileId()
        );


        // -------------------------------------------------
        // DELETE DATABASE RECORD
        // -------------------------------------------------

        claimDocumentRepository.delete(
                document
        );
    }


    // =========================================================
    // GET DOCUMENT FILE
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public StorageFile getDocumentFile(
            Long documentId
    ) {

        validateDocumentId(
                documentId
        );


        ClaimDocument document =
                findDocumentById(
                        documentId
                );


        return fileStorageService.download(
                document.getStorageFileId()
        );
    }


    // =========================================================
    // GET CLAIM FOLDER
    // =========================================================

    private String getClaimFolderId(
            Claim claim
    ) {

        return googleDriveProperties
                .getRootFolderId();
    }


    // =========================================================
    // VALIDATE FILE UPLOAD
    // =========================================================

    private void validateUploadRequest(

            MultipartFile file,

            ClaimDocumentType documentType,

            DocumentSource documentSource,

            String uploadedByEtfNo
    ) {

        if (file == null || file.isEmpty()) {

            throw new IllegalArgumentException(
                    "File is required"
            );
        }


        if (documentType == null) {

            throw new IllegalArgumentException(
                    "Document type is required"
            );
        }


        if (documentSource == null) {

            throw new IllegalArgumentException(
                    "Document source is required"
            );
        }


        if (uploadedByEtfNo == null
                || uploadedByEtfNo.isBlank()) {

            throw new IllegalArgumentException(
                    "Uploader ETF number is required"
            );
        }


        if (file.getSize() > MAX_FILE_SIZE) {

            throw new IllegalArgumentException(
                    "File size must not exceed 10 MB"
            );
        }


        String contentType =
                file.getContentType();


        if (contentType == null
                || !ALLOWED_CONTENT_TYPES
                .contains(contentType)) {

            throw new IllegalArgumentException(
                    "Only PDF, JPG and PNG files are allowed"
            );
        }
    }


    // =========================================================
    // FIND DOCUMENT
    // =========================================================

    private ClaimDocument findDocumentById(
            Long documentId
    ) {

        return claimDocumentRepository
                .findById(documentId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Document not found with ID: "
                                        + documentId
                        )
                );
    }


    // =========================================================
    // VALIDATE CLAIM ID
    // =========================================================

    private void validateClaimId(
            Long claimId
    ) {

        if (claimId == null
                || claimId <= 0) {

            throw new IllegalArgumentException(
                    "Claim ID must be a valid positive number"
            );
        }
    }


    // =========================================================
    // VALIDATE DOCUMENT ID
    // =========================================================

    private void validateDocumentId(
            Long documentId
    ) {

        if (documentId == null
                || documentId <= 0) {

            throw new IllegalArgumentException(
                    "Document ID must be a valid positive number"
            );
        }
    }


    // =========================================================
    // SANITIZE FILE NAME
    // =========================================================

    private String sanitizeFileName(
            String fileName
    ) {

        if (fileName == null
                || fileName.isBlank()) {

            return "unknown";
        }


        return Path.of(fileName)
                .getFileName()
                .toString();
    }


    // =========================================================
    // ENTITY -> DTO
    // =========================================================

    private ClaimDocumentDTO mapToDTO(
            ClaimDocument document
    ) {

        return ClaimDocumentDTO.builder()

                .id(
                        document.getId()
                )

                .claimId(
                        document.getClaim().getId()
                )

                .documentType(
                        document.getDocumentType()
                )

                .documentSource(
                        document.getDocumentSource()
                )

                .status(
                        document.getStatus()
                )

                .fileName(
                        document.getFileName()
                )

                // Secure backend endpoint.
                // Do NOT expose Google Drive file ID.
                .viewUrl(
                        "/api/claims/documents/"
                                + document.getId()
                                + "/view"
                )

                .contentType(
                        document.getContentType()
                )

                .fileSize(
                        document.getFileSize()
                )

                .uploadedByEtfNo(
                        document.getUploadedByEtfNo()
                )

                .rejectionReason(
                        document.getRejectionReason()
                )

                .uploadedAt(
                        document.getUploadedAt()
                )

                .updatedAt(
                        document.getUpdatedAt()
                )

                .build();
    }
}