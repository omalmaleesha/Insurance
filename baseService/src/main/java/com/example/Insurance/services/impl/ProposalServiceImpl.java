package com.example.Insurance.services.impl;

import com.example.Insurance.dto.proposal.*;
import com.example.Insurance.entities.propsal.Proposal;
import com.example.Insurance.entities.propsal.ProposalAccessToken;
import com.example.Insurance.entities.propsal.ProposalSignature;
import com.example.Insurance.repository.proposal.ProposalAccessTokenRepository;
import com.example.Insurance.repository.proposal.ProposalRepository;
import com.example.Insurance.repository.proposal.ProposalSignatureRepository;
import com.example.Insurance.services.ProposalService;
import com.example.Insurance.utils.types.ProposalStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProposalServiceImpl implements ProposalService {

    private final ProposalRepository proposalRepository;
    private final ProposalAccessTokenRepository tokenRepository;
    private final ProposalSignatureRepository signatureRepository;

    @Override
    public ProposalResponse createProposal(CreateProposalRequest request, Authentication authentication) {

        // Generate proposal number
        String proposalNumber = "PROP-" + System.currentTimeMillis();

        Proposal proposal = Proposal.builder()
                .proposalNumber(proposalNumber)
                .customerName(request.getCustomerName())
                .customerEmail(request.getCustomerEmail())
                .customerPhone(request.getCustomerPhone())
                .productName(request.getProductName())
                .status(ProposalStatus.DRAFT)
                .build();

        Proposal savedProposal = proposalRepository.save(proposal);

        return ProposalResponse.builder()
                .id(savedProposal.getId())
                .proposalNumber(savedProposal.getProposalNumber())
                .customerName(savedProposal.getCustomerName())
                .customerEmail(savedProposal.getCustomerEmail())
                .customerPhone(savedProposal.getCustomerPhone())
                .productName(savedProposal.getProductName())
                .status(savedProposal.getStatus())
                .build();
    }

    @Override
    public ProposalResponse getProposal(Long id, Authentication authentication) {

        Proposal proposal = proposalRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Proposal not found with id : " + id));

        return ProposalResponse.builder()
                .id(proposal.getId())
                .proposalNumber(proposal.getProposalNumber())
                .customerName(proposal.getCustomerName())
                .customerEmail(proposal.getCustomerEmail())
                .customerPhone(proposal.getCustomerPhone())
                .productName(proposal.getProductName())
                .status(proposal.getStatus())
                .build();
    }

    @Override
    public List<ProposalResponse> getAllProposals(Authentication authentication) {

        return proposalRepository.findAll()
                .stream()
                .map(proposal -> ProposalResponse.builder()
                        .id(proposal.getId())
                        .proposalNumber(proposal.getProposalNumber())
                        .customerName(proposal.getCustomerName())
                        .customerEmail(proposal.getCustomerEmail())
                        .customerPhone(proposal.getCustomerPhone())
                        .productName(proposal.getProductName())
                        .status(proposal.getStatus())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public ProposalFormResponse getProposalByToken(String token) {

        ProposalAccessToken accessToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid proposal token."));

        if (accessToken.isUsed()) {
            throw new RuntimeException("This proposal has already been submitted.");
        }

        if (accessToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Proposal link has expired.");
        }

        Proposal proposal = accessToken.getProposal();

        // Mark proposal as opened (only once)
        if (proposal.getStatus() == ProposalStatus.EMAIL_SENT) {
            proposal.setStatus(ProposalStatus.OPENED);
            proposal.setOpenedAt(LocalDateTime.now());
            proposalRepository.save(proposal);
        }

        return ProposalFormResponse.builder()
                .proposalNumber(proposal.getProposalNumber())
                .customerName(proposal.getCustomerName())
                .customerEmail(proposal.getCustomerEmail())
                .status(proposal.getStatus())
                .build();
    }

    @Override
    public ProposalFormResponse submitProposal(ProposalFormRequest request) {

        ProposalAccessToken accessToken = tokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new RuntimeException("Invalid proposal token."));

        if (accessToken.isUsed()) {
            throw new RuntimeException("Proposal has already been submitted.");
        }

        if (accessToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Proposal link has expired.");
        }

        Proposal proposal = accessToken.getProposal();

        // Update proposal information
        proposal.setCustomerName(request.getCustomerName());
        proposal.setCustomerPhone(request.getCustomerPhone());

        proposal.setStatus(ProposalStatus.SUBMITTED);
        proposal.setSubmittedAt(LocalDateTime.now());

        proposalRepository.save(proposal);

        // Save customer signature
        ProposalSignature signature = ProposalSignature.builder()
                .proposal(proposal)
                .signatureBase64(request.getSignatureBase64())
                .signedBy(request.getCustomerName())
                .signedAt(LocalDateTime.now())
                .build();

        signatureRepository.save(signature);

        // Mark token as used
        accessToken.setUsed(true);
        tokenRepository.save(accessToken);

        return ProposalFormResponse.builder()
                .proposalNumber(proposal.getProposalNumber())
                .customerName(proposal.getCustomerName())
                .customerEmail(proposal.getCustomerEmail())
                .status(proposal.getStatus())
                .build();
    }
}