package com.example.Insurance.repository;

import com.example.Insurance.entities.GReportGenerationJob;
import com.example.Insurance.utils.types.GReportJobStatus;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface GReportGenerationJobRepository extends JpaRepository<GReportGenerationJob, Long> {

    Optional<GReportGenerationJob> findByClaimId(Long claimId);

    boolean existsByClaimId(Long claimId);

    boolean existsByClaimIdAndStatus(
            Long claimId,
            GReportJobStatus status
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT j
            FROM GReportGenerationJob j
            WHERE j.id = :jobId
            AND j.status = :status
            """)
    Optional<GReportGenerationJob> findAndLockJob(
            @Param("jobId") Long jobId,
            @Param("status") GReportJobStatus status
    );

    List<GReportGenerationJob> findTop5ByStatusOrderByCreatedAtAsc(
            GReportJobStatus status
    );

    @Query("""
        SELECT j
        FROM GReportGenerationJob j
        WHERE j.status = :status
        AND j.lockedAt < :cutoff
        """)
    List<GReportGenerationJob> findStaleJobs(
            @Param("status") GReportJobStatus status,
            @Param("cutoff") LocalDateTime cutoff
    );
}