package com.example.Insurance.repository;

import com.example.Insurance.entities.ProposalEmailLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProposalEmailLogRepository extends JpaRepository<ProposalEmailLog, Long> {

    List<ProposalEmailLog> findByProposalId(Long proposalId);

}
