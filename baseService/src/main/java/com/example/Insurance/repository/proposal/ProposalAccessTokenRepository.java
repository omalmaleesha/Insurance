package com.example.Insurance.repository.proposal;

import com.example.Insurance.entities.propsal.ProposalAccessToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProposalAccessTokenRepository extends JpaRepository<ProposalAccessToken, Long> {

    Optional<ProposalAccessToken> findByToken(String token);

    Optional<ProposalAccessToken> findByProposalId(Long proposalId);

}