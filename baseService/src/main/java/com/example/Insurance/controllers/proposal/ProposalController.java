package com.example.Insurance.controllers.proposal;


import com.example.Insurance.dto.proposal.*;
import com.example.Insurance.services.EmailService;
import com.example.Insurance.services.ProposalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/proposals")
@RequiredArgsConstructor
@CrossOrigin
public class ProposalController {

    private final ProposalService proposalService;
    private final EmailService emailService;

    // Create proposal
    @PostMapping
    public ResponseEntity<ProposalResponse> createProposal(
            @Validated @RequestBody CreateProposalRequest request,
            Authentication authentication) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(proposalService.createProposal(request, authentication));
    }


    @PreAuthorize("hasAuthority('EMAIL_SEND')")
    @PostMapping("/{proposalId}/send")
    public ResponseEntity<EmailResponse> sendProposalToCustomer(
            @PathVariable Long proposalId,
            Authentication authentication) {

        return ResponseEntity.ok(
                emailService.sendProposalEmail(proposalId, authentication));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProposalResponse> getProposal(
            @PathVariable Long id,
            Authentication authentication) {

        return ResponseEntity.ok(
                proposalService.getProposal(id, authentication));
    }

    @GetMapping
    public ResponseEntity<List<ProposalResponse>> getAllProposals(
            Authentication authentication) {

        return ResponseEntity.ok(
                proposalService.getAllProposals(authentication));
    }

    // Customer opens proposal
    @GetMapping("/form")
    public ResponseEntity<ProposalFormResponse> getProposalForm(
            @RequestParam String token) {

        return ResponseEntity.ok(
                proposalService.getProposalByToken(token));
    }

    // Customer submits proposal
    @PostMapping("/submit")
    public ResponseEntity<ProposalFormResponse> submitProposal(
            @Validated @RequestBody ProposalFormRequest request) {

        return ResponseEntity.ok(
                proposalService.submitProposal(request));
    }
}