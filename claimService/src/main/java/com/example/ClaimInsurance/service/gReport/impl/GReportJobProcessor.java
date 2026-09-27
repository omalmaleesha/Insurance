package com.example.ClaimInsurance.service.gReport.impl;

import com.example.ClaimInsurance.entities.GReportGenerationJob;
import com.example.ClaimInsurance.repository.GReportGenerationJobRepository;
import com.example.ClaimInsurance.service.gReport.GReportGenerationService;
import com.example.ClaimInsurance.service.gReport.GReportJobService;
import com.example.ClaimInsurance.utils.types.GReportJobStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class GReportJobProcessor {

    private final GReportGenerationJobRepository jobRepository;
    private final GReportJobService jobService;
    private final GReportGenerationService generationService;

    @Scheduled(fixedDelay = 5000)
    public void processPendingJobs() {
        List<GReportGenerationJob> jobs =
                jobRepository
                        .findTop5ByStatusOrderByCreatedAtAsc(
                                GReportJobStatus.PENDING
                        );
        for (GReportGenerationJob job : jobs) {
            boolean started = jobService.startJob(job.getId());
            if (!started) {
                continue;
            }
            processJob(job.getId());
        }
    }

    private void processJob(Long jobId) {
        try {
            generationService.generate(jobId);
            jobService.completeJob(jobId);
        } catch (Exception e) {
            log.error(
                    "G-report generation failed. jobId={}",
                    jobId,
                    e
            );
            jobService.failJob(
                    jobId,
                    e.getMessage()
            );
        }
    }

    @Scheduled(fixedDelay = 60000)
    public void recoverStaleJobs() {

        LocalDateTime cutoff =
                LocalDateTime.now().minusMinutes(10);

        List<GReportGenerationJob> staleJobs =
                jobRepository.findStaleJobs(
                        GReportJobStatus.PROCESSING,
                        cutoff
                );

        for (GReportGenerationJob job : staleJobs) {

            log.warn(
                    "Recovering stale G-report job. jobId={}, claimId={}",
                    job.getId(),
                    job.getClaim().getId()
            );

            jobService.failJob(
                    job.getId(),
                    "Job became stale while processing."
            );
        }
    }
}