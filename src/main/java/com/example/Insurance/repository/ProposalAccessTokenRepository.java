package com.example.Insurance.repository;

import com.example.Insurance.entities.ProposalAccessToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProposalAccessTokenRepository extends JpaRepository<ProposalAccessToken, Long> {

    Optional<ProposalAccessToken> findByToken(String token);

    Optional<ProposalAccessToken> findByProposalId(Long proposalId);

}