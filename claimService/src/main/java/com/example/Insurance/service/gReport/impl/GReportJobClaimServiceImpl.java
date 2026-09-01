package com.example.Insurance.service.gReport.impl;

import com.example.Insurance.entities.GReportGenerationJob;
import com.example.Insurance.repository.GReportGenerationJobRepository;
import com.example.Insurance.service.gReport.GReportJobClaimService;
import com.example.Insurance.utils.types.GReportJobStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class GReportJobClaimServiceImpl implements GReportJobClaimService {

    private final GReportGenerationJobRepository jobRepository;

    @Override
    @Transactional
    public boolean claimJob(Long jobId, String workerId) {
        GReportGenerationJob job =
                jobRepository.findAndLockJob(
                        jobId,
                        GReportJobStatus.PENDING
                ).orElse(null);

        if (job == null) {
            return false;
        }
        job.setStatus(GReportJobStatus.CLAIMED);
        job.setWorkerId(workerId);
        job.setLockedAt(LocalDateTime.now());

        jobRepository.save(job);

        return true;
    }
}