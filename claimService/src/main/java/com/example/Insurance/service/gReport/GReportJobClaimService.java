package com.example.Insurance.service.gReport;

public interface GReportJobClaimService {
    boolean claimJob(
            Long jobId,
            String workerId
    );
}