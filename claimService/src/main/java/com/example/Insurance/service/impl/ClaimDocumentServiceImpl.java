package com.example.Insurance.service.impl;

import com.example.Insurance.dto.ClaimDocumentDTO;
import com.example.Insurance.entities.Claim;
import com.example.Insurance.entities.ClaimDocument;
import com.example.Insurance.repository.ClaimDocumentRepository;
import com.example.Insurance.repository.ClaimRepository;
import com.example.Insurance.service.ClaimDocumentService;
import com.example.Insurance.utils.types.ClaimDocumentType;
import com.example.Insurance.utils.types.DocumentSource;
import com.example.Insurance.utils.types.DocumentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ClaimDocumentServiceImpl implements ClaimDocumentService {

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10 MB
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of("application/pdf", "image/jpeg", "image/png");
    private final ClaimDocumentRepository claimDocumentRepository;
    private final ClaimRepository claimRepository;
    @Value("${file.upload-dir:uploads/claims}")
    private String uploadDirectory;

    // =========================================================
    // UPLOAD DOCUMENT
    // =========================================================
    @Override
    public ClaimDocumentDTO uploadDocument(Long claimId, MultipartFile file, ClaimDocumentType documentType, DocumentSource documentSource, String uploadedByEtfNo) {

        validateClaimId(claimId);
        validateUploadRequest(file, documentType, documentSource, uploadedByEtfNo);

        Claim claim = claimRepository.findById(claimId).orElseThrow(() -> new RuntimeException("Claim not found with ID: " + claimId));

        String storedFileName;
        Path destinationPath;

        try {

            Path uploadPath = Path.of(uploadDirectory).toAbsolutePath().normalize();

            Files.createDirectories(uploadPath);

            String originalFileName = file.getOriginalFilename();

            String extension = getFileExtension(originalFileName);

            storedFileName = UUID.randomUUID() + extension;

            destinationPath = uploadPath.resolve(storedFileName).normalize();

            // Prevent path traversal
            if (!destinationPath.startsWith(uploadPath)) {

                throw new IllegalArgumentException("Invalid file path");
            }

            Files.copy(file.getInputStream(), destinationPath, StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException e) {

            throw new RuntimeException("Failed to store document", e);
        }


        ClaimDocument document = ClaimDocument.builder()

                .claim(claim)

                .documentType(documentType)

                .documentSource(documentSource)

                .status(DocumentStatus.UPLOADED)

                .fileName(sanitizeFileName(file.getOriginalFilename()))

                .fileUrl(destinationPath.toString())

                .contentType(file.getContentType())

                .fileSize(file.getSize())

                .uploadedByEtfNo(uploadedByEtfNo.trim())

                .build();


        ClaimDocument savedDocument = claimDocumentRepository.save(document);

        return mapToDTO(savedDocument);
    }


    // =========================================================
    // GET DOCUMENTS BY CLAIM ID
    // =========================================================
    @Override
    @Transactional(readOnly = true)
    public List<ClaimDocumentDTO> getDocumentsByClaimId(Long claimId) {

        validateClaimId(claimId);

        if (!claimRepository.existsById(claimId)) {

            throw new RuntimeException("Claim not found with ID: " + claimId);
        }

        return claimDocumentRepository.findByClaimId(claimId).stream().map(this::mapToDTO).toList();
    }


    // =========================================================
    // GET DOCUMENT BY ID
    // =========================================================
    @Override
    @Transactional(readOnly = true)
    public ClaimDocumentDTO getDocumentById(Long documentId) {

        validateDocumentId(documentId);

        ClaimDocument document = findDocumentById(documentId);

        return mapToDTO(document);
    }


    // =========================================================
    // VERIFY DOCUMENT
    // =========================================================
    @Override
    public ClaimDocumentDTO verifyDocument(Long documentId) {

        validateDocumentId(documentId);

        ClaimDocument document = findDocumentById(documentId);

        if (document.getStatus() == DocumentStatus.REJECTED) {

            throw new IllegalStateException("Rejected document cannot be verified");
        }

        if (document.getStatus() == DocumentStatus.VERIFIED) {

            throw new IllegalStateException("Document is already verified");
        }

        document.setStatus(DocumentStatus.VERIFIED);

        document.setRejectionReason(null);

        ClaimDocument updatedDocument = claimDocumentRepository.save(document);

        return mapToDTO(updatedDocument);
    }


    // =========================================================
    // REJECT DOCUMENT
    // =========================================================
    @Override
    public ClaimDocumentDTO rejectDocument(Long documentId, String reason) {

        validateDocumentId(documentId);

        if (reason == null || reason.isBlank()) {

            throw new IllegalArgumentException("Rejection reason is required");
        }

        ClaimDocument document = findDocumentById(documentId);

        if (document.getStatus() == DocumentStatus.VERIFIED) {

            throw new IllegalStateException("Verified document cannot be rejected");
        }

        document.setStatus(DocumentStatus.REJECTED);

        document.setRejectionReason(reason.trim());

        ClaimDocument updatedDocument = claimDocumentRepository.save(document);

        return mapToDTO(updatedDocument);
    }


    // =========================================================
    // DELETE DOCUMENT
    // =========================================================
    @Override
    public void deleteDocument(Long documentId) {

        validateDocumentId(documentId);

        ClaimDocument document = findDocumentById(documentId);

        deletePhysicalFile(document.getFileUrl());

        claimDocumentRepository.delete(document);
    }


    // =========================================================
    // VALIDATE FILE UPLOAD
    // =========================================================
    private void validateUploadRequest(MultipartFile file, ClaimDocumentType documentType, DocumentSource documentSource, String uploadedByEtfNo) {

        if (file == null || file.isEmpty()) {

            throw new IllegalArgumentException("File is required");
        }

        if (documentType == null) {

            throw new IllegalArgumentException("Document type is required");
        }

        if (documentSource == null) {

            throw new IllegalArgumentException("Document source is required");
        }

        if (uploadedByEtfNo == null || uploadedByEtfNo.isBlank()) {

            throw new IllegalArgumentException("Uploader ETF number is required");
        }

        if (file.getSize() > MAX_FILE_SIZE) {

            throw new IllegalArgumentException("File size must not exceed 10 MB");
        }

        String contentType = file.getContentType();

        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {

            throw new IllegalArgumentException("Only PDF, JPG and PNG files are allowed");
        }
    }


    // =========================================================
    // FIND DOCUMENT
    // =========================================================
    private ClaimDocument findDocumentById(Long documentId) {

        return claimDocumentRepository.findById(documentId).orElseThrow(() -> new RuntimeException("Document not found with ID: " + documentId));
    }


    // =========================================================
    // VALIDATE CLAIM ID
    // =========================================================
    private void validateClaimId(Long claimId) {

        if (claimId == null || claimId <= 0) {

            throw new IllegalArgumentException("Claim ID must be a valid positive number");
        }
    }


    // =========================================================
    // VALIDATE DOCUMENT ID
    // =========================================================
    private void validateDocumentId(Long documentId) {

        if (documentId == null || documentId <= 0) {

            throw new IllegalArgumentException("Document ID must be a valid positive number");
        }
    }


    // =========================================================
    // GET FILE EXTENSION
    // =========================================================
    private String getFileExtension(String fileName) {

        if (fileName == null || !fileName.contains(".")) {

            return "";
        }

        return fileName.substring(fileName.lastIndexOf("."));
    }


    // =========================================================
    // SANITIZE FILE NAME
    // =========================================================
    private String sanitizeFileName(String fileName) {

        if (fileName == null) {
            return "unknown";
        }

        return Path.of(fileName).getFileName().toString();
    }


    // =========================================================
    // DELETE PHYSICAL FILE
    // =========================================================
    private void deletePhysicalFile(String filePath) {

        if (filePath == null || filePath.isBlank()) {
            return;
        }

        try {

            Path path = Path.of(filePath);

            Files.deleteIfExists(path);

        } catch (IOException e) {

            throw new RuntimeException("Failed to delete physical document", e);
        }
    }


    // =========================================================
    // ENTITY -> DTO
    // =========================================================
    private ClaimDocumentDTO mapToDTO(ClaimDocument document) {

        return ClaimDocumentDTO.builder()

                .id(document.getId())

                .claimId(document.getClaim().getId())

                .documentType(document.getDocumentType())

                .documentSource(document.getDocumentSource())

                .status(document.getStatus())

                .fileName(document.getFileName())

                .fileUrl(document.getFileUrl())

                .contentType(document.getContentType())

                .fileSize(document.getFileSize())

                .uploadedByEtfNo(document.getUploadedByEtfNo())

                .rejectionReason(document.getRejectionReason())

                .uploadedAt(document.getUploadedAt())

                .updatedAt(document.getUpdatedAt())

                .build();
    }
}