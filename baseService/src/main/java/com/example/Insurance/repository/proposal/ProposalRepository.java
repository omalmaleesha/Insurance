package com.example.Insurance.repository.proposal;


import com.example.Insurance.entities.propsal.Proposal;
import com.example.Insurance.utils.types.ProposalStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProposalRepository extends JpaRepository<Proposal, Long> {

    Optional<Proposal> findByProposalNumber(String proposalNumber);

    List<Proposal> findByStatus(ProposalStatus status);

    List<Proposal> findByCustomerEmail(String customerEmail);

}