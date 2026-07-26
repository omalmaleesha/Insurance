package com.example.Insurance.repository;

import com.example.Insurance.entities.ProposalSignature;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProposalSignatureRepository extends JpaRepository<ProposalSignature, Long> {

    Optional<ProposalSignature> findByProposalId(Long proposalId);

}