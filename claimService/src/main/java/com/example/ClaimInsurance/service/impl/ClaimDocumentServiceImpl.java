package com.example.ClaimInsurance.service.impl;
import com.example.ClaimInsurance.config.GoogleDriveProperties;
import com.example.ClaimInsurance.dto.ClaimDocumentDTO;
import com.example.ClaimInsurance.entities.Claim;
import com.example.ClaimInsurance.entities.ClaimDocument;
import com.example.ClaimInsurance.repository.ClaimDocumentRepository;
import com.example.ClaimInsurance.repository.ClaimRepository;
import com.example.ClaimInsurance.service.ClaimDocumentService;
import com.example.ClaimInsurance.service.gReport.GReportJobService;
import com.example.ClaimInsurance.service.storage.FileStorageService;
import com.example.ClaimInsurance.service.storage.StoredFile;
import com.example.ClaimInsurance.service.storage.StorageFile;
import com.example.ClaimInsurance.utils.types.ClaimDocumentType;
import com.example.ClaimInsurance.utils.types.DocumentSource;
import com.example.ClaimInsurance.utils.types.DocumentStatus;
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
public class ClaimDocumentServiceImpl implements ClaimDocumentService {

    private final GReportJobService gReportJobService;
    private static final long MAX_FILE_SIZE =
            10 * 1024 * 1024;

    private static final Set<String> ALLOWED_CONTENT_TYPES =
            Set.of(
                    "application/pdf",
                    "image/jpeg",
                    "image/png"
            );

    private final ClaimDocumentRepository claimDocumentRepository;
    private final ClaimRepository claimRepository;
    private final FileStorageService fileStorageService;
    private final GoogleDriveProperties googleDriveProperties;

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
                        .orElseThrow(() -> new RuntimeException("Claim not found with ID: " + claimId));

        StoredFile storedFile = null;

        try {
            storedFile = fileStorageService.upload(file, getClaimFolderId(claim));
            ClaimDocument document =
                    ClaimDocument.builder()
                            .claim(claim)
                            .documentType(documentType)
                            .documentSource(documentSource)
                            .status(DocumentStatus.UPLOADED)
                            .fileName(sanitizeFileName(file.getOriginalFilename()))
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

            ClaimDocument savedDocument =
                    claimDocumentRepository.save(
                            document
                    );

            return mapToDTO(
                    savedDocument
            );

        } catch (Exception e) {
            if (storedFile != null) {
                try {
                    fileStorageService.delete(
                            storedFile.fileId()
                    );

                } catch (Exception cleanupException) {

                }
            }
            throw e;
        }
    }

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

        document.setStatus(DocumentStatus.VERIFIED);
        ClaimDocument saved = claimDocumentRepository.save(document);
        checkAndCreateGReportJob(document.getClaim().getId());
        return mapToDTO(saved);
    }

    private void checkAndCreateGReportJob(Long claimId) {
        boolean claimFormVerified = claimDocumentRepository.existsByClaimIdAndDocumentTypeAndStatus(claimId, ClaimDocumentType.CLAIM_FORM, DocumentStatus.VERIFIED);
        boolean customerStatementVerified =
                claimDocumentRepository
                        .existsByClaimIdAndDocumentTypeAndStatus(
                                claimId,
                                ClaimDocumentType.CUSTOMER_STATEMENT,
                                DocumentStatus.VERIFIED
                        );

        if (!claimFormVerified ||
                !customerStatementVerified) {

            return;
        }
        gReportJobService.createJobIfNotExists(claimId);
    }

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

        fileStorageService.delete(
                document.getStorageFileId()
        );
        claimDocumentRepository.delete(
                document
        );
    }
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
    private String getClaimFolderId(
            Claim claim
    ) {
        return googleDriveProperties
                .getRootFolderId();
    }

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