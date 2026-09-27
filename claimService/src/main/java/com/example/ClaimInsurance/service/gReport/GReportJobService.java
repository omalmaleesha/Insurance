package com.example.ClaimInsurance.service.gReport;

public interface GReportJobService {
    void createJobIfNotExists(Long claimId);
    boolean startJob(Long jobId);
    void completeJob(Long jobId);
    void failJob(
            Long jobId,
            String errorMessage
    );
}