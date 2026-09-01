package com.example.Insurance.service.gReport.impl;
import com.example.Insurance.entities.Claim;
import com.example.Insurance.entities.GReportGenerationJob;
import com.example.Insurance.repository.ClaimRepository;
import com.example.Insurance.repository.GReportGenerationJobRepository;
import com.example.Insurance.service.gReport.GReportJobService;
import com.example.Insurance.utils.types.ClaimStatus;
import com.example.Insurance.utils.types.GReportJobStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class GReportJobServiceImpl implements GReportJobService {

    private static final int MAX_ATTEMPTS = 3;

    private final GReportGenerationJobRepository jobRepository;
    private final ClaimRepository claimRepository;

    @Override
    @Transactional
    public boolean startJob(Long jobId) {
        GReportGenerationJob job =
                jobRepository.findAndLockJob(
                        jobId,
                        GReportJobStatus.PENDING
                ).orElse(null);

        if (job == null) {
            return false;
        }

        job.setStatus(
                GReportJobStatus.PROCESSING
        );

        job.setStartedAt(
                LocalDateTime.now()
        );

        job.setLockedAt(
                LocalDateTime.now()
        );

        job.setWorkerId(
                "g-report-worker"
        );

        job.setAttemptCount(
                job.getAttemptCount() + 1
        );

        jobRepository.save(job);

        log.info(
                "G-report job started. jobId={}, claimId={}, attempt={}",
                job.getId(),
                job.getClaim().getId(),
                job.getAttemptCount()
        );

        return true;
    }

    @Override
    @Transactional
    public void completeJob(Long jobId) {

        GReportGenerationJob job =
                jobRepository.findById(jobId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "G-report job not found: " + jobId
                                )
                        );

        job.setStatus(
                GReportJobStatus.COMPLETED
        );

        job.setCompletedAt(
                LocalDateTime.now()
        );

        job.setLockedAt(null);
        job.setWorkerId(null);
        job.setLastError(null);

        jobRepository.save(job);

        log.info(
                "G-report job completed. jobId={}, claimId={}",
                job.getId(),
                job.getClaim().getId()
        );
    }

    @Override
    @Transactional
    public void createJobIfNotExists(Long claimId) {
        if (jobRepository.existsByClaimId(claimId)) {
            log.info(
                    "G-report job already exists. claimId={}",
                    claimId
            );
            return;
        }

        Claim claim = claimRepository.findById(claimId).orElseThrow(() -> new RuntimeException("Claim not found: " + claimId));

        GReportGenerationJob job =
                GReportGenerationJob.builder()
                        .claim(claim)
                        .status(GReportJobStatus.PENDING)
                        .attemptCount(0)
                        .build();

        claim.setStatus(ClaimStatus.PENDING_G_REPORT);
        claimRepository.save(claim);

        jobRepository.save(job);

        log.info(
                "G-report job created. claimId={}, jobId={}",
                claimId,
                job.getId()
        );
    }

    @Override
    @Transactional
    public void failJob(
            Long jobId,
            String errorMessage
    ) {

        GReportGenerationJob job =
                jobRepository.findById(jobId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "G-report job not found: " + jobId
                                )
                        );

        job.setLastError(errorMessage);
        job.setLockedAt(null);
        job.setWorkerId(null);

        if (job.getAttemptCount() >= MAX_ATTEMPTS) {

            job.setStatus(
                    GReportJobStatus.MANUAL_REVIEW
            );

        } else {

            job.setStatus(
                    GReportJobStatus.PENDING
            );
        }

        jobRepository.save(job);

        log.error(
                "G-report job failed. jobId={}, claimId={}, attempt={}, nextStatus={}",
                job.getId(),
                job.getClaim().getId(),
                job.getAttemptCount(),
                job.getStatus()
        );
    }
}