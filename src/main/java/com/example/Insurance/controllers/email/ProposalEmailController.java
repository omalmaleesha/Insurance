package com.example.Insurance.controllers.email;


import com.example.Insurance.dto.proposal.EmailResponse;
import com.example.Insurance.services.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/proposal-emails")
@RequiredArgsConstructor
@CrossOrigin
public class ProposalEmailController {

    private final EmailService emailService;

    /**
     * Send proposal email containing secure link
     */
    @PostMapping("/send/{proposalId}")
    public ResponseEntity<EmailResponse> sendProposalEmail(
            @PathVariable Long proposalId, Authentication authentication) {

        EmailResponse response = emailService.sendProposalEmail(proposalId,authentication);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    /**
     * Re-send proposal email
     */
    @PostMapping("/resend/{proposalId}")
    public ResponseEntity<EmailResponse> resendProposalEmail(
            @PathVariable Long proposalId, Authentication authentication) {

        EmailResponse response = emailService.resendProposalEmail(proposalId);

        return ResponseEntity.ok(response);
    }

    /**
     * Send customer copy after proposal submission
     */
    @PostMapping("/customer-copy/{proposalId}")
    public ResponseEntity<EmailResponse> sendCustomerCopy(
            @PathVariable Long proposalId, Authentication authentication) {

        EmailResponse response = emailService.sendCustomerCopy(proposalId);

        return ResponseEntity.ok(response);
    }

}
