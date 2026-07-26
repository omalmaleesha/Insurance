package com.example.Insurance.repository;


import com.example.Insurance.entities.Proposal;
import com.example.Insurance.utils.ProposalStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProposalRepository extends JpaRepository<Proposal, Long> {

    Optional<Proposal> findByProposalNumber(String proposalNumber);

    List<Proposal> findByStatus(ProposalStatus status);

    List<Proposal> findByCustomerEmail(String customerEmail);

}