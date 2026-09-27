package com.example.ClaimInsurance.dto;
import com.example.ClaimInsurance.utils.types.ClaimDocumentType;
import com.example.ClaimInsurance.utils.types.DocumentSource;
import com.example.ClaimInsurance.utils.types.DocumentStatus;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimDocumentDTO {
    private Long id;
    private Long claimId;
    private ClaimDocumentType documentType;
    private DocumentSource documentSource;
    private DocumentStatus status;
    private String fileName;
    private String viewUrl;
    private String contentType;
    private Long fileSize;
    private String uploadedByEtfNo;
    private String rejectionReason;
    private LocalDateTime uploadedAt;
    private LocalDateTime updatedAt;
}