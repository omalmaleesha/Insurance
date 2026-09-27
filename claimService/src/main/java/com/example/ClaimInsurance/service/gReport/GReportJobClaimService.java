package com.example.ClaimInsurance.service.gReport;

public interface GReportJobClaimService {
    boolean claimJob(
            Long jobId,
            String workerId
    );
}