package com.example.ClaimInsurance.dto;

import com.example.ClaimInsurance.utils.types.ClaimStatus;
import com.example.ClaimInsurance.utils.types.ClaimType;
import jakarta.persistence.Column;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimDTO {
    private Long id;
    private String claimNumber;
    private Long customerId;
    private String createdByEtfNo;
    private ClaimType claimType;
    private ClaimStatus status;
    private LocalDateTime incidentDate;
    private LocalDateTime reportedDate;
    private String policyNumber;
    private String incidentLocation;
    private String incidentDescription;
    private String situationStatement;
    private String branchCode;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}