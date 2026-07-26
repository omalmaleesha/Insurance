package com.example.Insurance.services;

import com.example.Insurance.dto.proposal.EmailResponse;
import org.springframework.security.core.Authentication;

public interface EmailService {

    EmailResponse sendProposalEmail(Long proposalId, Authentication authentication);

    EmailResponse resendProposalEmail(Long proposalId);

    EmailResponse sendCustomerCopy(Long proposalId);

}