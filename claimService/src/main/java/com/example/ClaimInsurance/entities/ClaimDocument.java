package com.example.ClaimInsurance.entities;

import com.example.ClaimInsurance.utils.types.ClaimDocumentType;
import com.example.ClaimInsurance.utils.types.DocumentSource;

import com.example.ClaimInsurance.utils.types.DocumentStatus;
import com.example.ClaimInsurance.utils.types.StorageProvider;
import jakarta.persistence.*;

import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "claim_documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Document belongs to a claim
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "claim_id",
            nullable = false
    )
    private Claim claim;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "document_type",
            nullable = false
    )
    private ClaimDocumentType documentType;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "document_source",
            nullable = false
    )
    private DocumentSource documentSource;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false
    )
    private DocumentStatus status;

    // Original filename
    @Column(
            name = "file_name",
            nullable = false
    )
    private String fileName;

    // Stored file path or cloud URL
    @Enumerated(EnumType.STRING)
    @Column(
            name = "storage_provider",
            nullable = false
    )
    private StorageProvider storageProvider;

    @Column(
            name = "storage_file_id",
            nullable = false,
            unique = true,
            length = 255
    )
    private String storageFileId;

    @Column(
            name = "storage_folder_id",
            length = 255
    )
    private String storageFolderId;

    // application/pdf, image/jpeg, etc.
    @Column(
            name = "content_type"
    )
    private String contentType;

    @Column(
            name = "file_size"
    )
    private Long fileSize;

    // ETF number from base-service
    @Column(
            name = "uploaded_by_etf_no",
            nullable = false,
            length = 20
    )
    private String uploadedByEtfNo;

    // Reason if rejected
    @Column(
            name = "rejection_reason",
            columnDefinition = "TEXT"
    )
    private String rejectionReason;

    @Column(
            name = "uploaded_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime uploadedAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {

        if (status == null) {
            status = DocumentStatus.UPLOADED;
        }

        uploadedAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @Column(
            name = "file_hash",
            length = 64
    )
    private String fileHash;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(
            name = "verified_by_etf_no",
            length = 20
    )
    private String verifiedByEtfNo;

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}