package com.example.Insurance.repository.proposal;

import com.example.Insurance.entities.propsal.ProposalSignature;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProposalSignatureRepository extends JpaRepository<ProposalSignature, Long> {

    Optional<ProposalSignature> findByProposalId(Long proposalId);

}