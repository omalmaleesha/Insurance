package com.example.Insurance.services;

import com.example.Insurance.dto.proposal.*;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface ProposalService {

    ProposalResponse createProposal(CreateProposalRequest request, Authentication authentication);

    ProposalResponse getProposal(Long id, Authentication authentication);

    List<ProposalResponse> getAllProposals( Authentication authentication);

    ProposalFormResponse getProposalByToken(String token);

    ProposalFormResponse submitProposal(ProposalFormRequest request);

}