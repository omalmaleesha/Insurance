package com.example.ClaimInsurance.dto;

import com.example.ClaimInsurance.utils.types.GReportJobStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GReportResponseDTO {

    private Long id;

    private Long claimId;

    private GReportJobStatus status;

    private Integer attemptCount;

    private String errorMessage;

    private LocalDateTime createdAt;

    private LocalDateTime startedAt;

    private LocalDateTime completedAt;

    private String workerId;

    private LocalDateTime lockedAt;

    private String lastError;
}