package com.example.Insurance.services.impl;


import com.example.Insurance.dto.proposal.EmailResponse;
import com.example.Insurance.entities.Proposal;
import com.example.Insurance.entities.ProposalAccessToken;
import com.example.Insurance.entities.ProposalEmailLog;
import com.example.Insurance.repository.ProposalAccessTokenRepository;
import com.example.Insurance.repository.ProposalEmailLogRepository;
import com.example.Insurance.repository.ProposalRepository;
import com.example.Insurance.services.EmailService;
import com.example.Insurance.services.PdfService;
import com.example.Insurance.utils.ProposalEmailType;
import com.example.Insurance.utils.ProposalStatus;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final ProposalRepository proposalRepository;
    private final ProposalAccessTokenRepository tokenRepository;
    private final ProposalEmailLogRepository emailLogRepository;
    private final JavaMailSender mailSender;
    private final PdfService pdfService;

    @Value("${proposal.frontend.url}")
    private String proposalFrontendUrl;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public EmailResponse sendProposalEmail(Long proposalId, Authentication authentication) {

        Proposal proposal = proposalRepository.findById(proposalId)
                .orElseThrow(() ->
                        new RuntimeException("Proposal not found"));

        String token = UUID.randomUUID().toString();

        ProposalAccessToken accessToken =
                tokenRepository.findByProposalId(proposalId)
                        .orElse(
                                ProposalAccessToken.builder()
                                        .proposal(proposal)
                                        .build()
                        );

        accessToken.setToken(token);
        accessToken.setUsed(false);
        accessToken.setExpiryDate(LocalDateTime.now().plusDays(7));

        tokenRepository.save(accessToken);

        String proposalLink =
                proposalFrontendUrl + "?token=" + token;

        String html = """
            <html>
            <body style="font-family:Arial,sans-serif;">
            
            <h2>Insurance Proposal</h2>

            <p>Dear %s,</p>

            <p>
            You have received an insurance proposal.
            Please click the button below to complete and sign your proposal.
            </p>

            <br>

            <a href="%s"
               style="
                    background:#0d6efd;
                    color:white;
                    padding:12px 24px;
                    text-decoration:none;
                    border-radius:5px;
                    font-weight:bold;">
                    Complete Proposal
            </a>

            <br><br>

            <p>
            This link will expire in <b>7 days</b>.
            </p>

            <p>
            Proposal Number:
            <b>%s</b>
            </p>

            <br>

            <p>
            Thank you.
            </p>

            </body>
            </html>
            """.formatted(
                proposal.getCustomerName(),
                proposalLink,
                proposal.getProposalNumber());

        try {

            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true);

            helper.setFrom(fromEmail);
            helper.setTo(proposal.getCustomerEmail());
            helper.setSubject("Insurance Proposal");
            helper.setText(html, true);

            mailSender.send(message);

        } catch (MessagingException e) {

            throw new RuntimeException("Failed to send email.", e);

        }

        ProposalEmailLog log = ProposalEmailLog.builder()
                .proposal(proposal)
                .recipient(proposal.getCustomerEmail())
                .subject("Insurance Proposal")
                .emailType(ProposalEmailType.PROPOSAL_LINK)
                .sentAt(LocalDateTime.now())
                .build();

        emailLogRepository.save(log);

        proposal.setStatus(ProposalStatus.EMAIL_SENT);
        proposal.setSentAt(LocalDateTime.now());

        proposalRepository.save(proposal);

        return EmailResponse.builder()
                .success(true)
                .message("Proposal email sent successfully.")
                .build();
    }

    @Override
    public EmailResponse resendProposalEmail(Long proposalId) {

        Proposal proposal = proposalRepository.findById(proposalId)
                .orElseThrow(() ->
                        new RuntimeException("Proposal not found"));

        ProposalAccessToken token = tokenRepository
                .findByProposalId(proposalId)
                .orElseThrow(() ->
                        new RuntimeException("Proposal token not found"));

        // Generate new token
        token.setToken(UUID.randomUUID().toString());
        token.setUsed(false);
        token.setExpiryDate(LocalDateTime.now().plusDays(7));

        tokenRepository.save(token);

        String proposalLink =
                proposalFrontendUrl + "?token=" + token.getToken();

        String html = buildProposalEmail(
                proposal.getCustomerName(),
                proposal.getProposalNumber(),
                proposalLink
        );

        try {

            sendHtmlEmail(
                    proposal.getCustomerEmail(),
                    "Insurance Proposal (Resent)",
                    html
            );

        } catch (MessagingException e) {

            throw new RuntimeException("Unable to resend proposal email.", e);

        }

        saveEmailLog(
                proposal,
                ProposalEmailType.PROPOSAL_LINK,
                "Insurance Proposal (Resent)"
        );

        return EmailResponse.builder()
                .success(true)
                .message("Proposal email resent successfully.")
                .build();
    }

    @Override
    public EmailResponse sendCustomerCopy(Long proposalId) {

        Proposal proposal = proposalRepository.findById(proposalId)
                .orElseThrow(() ->
                        new RuntimeException("Proposal not found"));

        byte[] pdf =
                pdfService.generateProposalPdf(proposalId);

        try{

            MimeMessage message =
                    mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(message,true);

            helper.setFrom(fromEmail);

            helper.setTo(proposal.getCustomerEmail());

            helper.setSubject("Signed Insurance Proposal");

            helper.setText(buildCustomerCopyEmail(
                            proposal.getCustomerName(),
                            proposal.getProposalNumber()),
                    true);

            helper.addAttachment(
                    "Proposal-"+proposal.getProposalNumber()+".pdf",
                    new org.springframework.core.io.ByteArrayResource(pdf)
            );

            mailSender.send(message);

        }catch (Exception e){

            throw new RuntimeException(e);

        }

        saveEmailLog(
                proposal,
                ProposalEmailType.CUSTOMER_COPY,
                "Signed Proposal");

        return EmailResponse.builder()
                .success(true)
                .message("Customer copy emailed successfully.")
                .build();

    }

    private void sendHtmlEmail(String to,
                               String subject,
                               String html)
            throws MessagingException {

        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper =
                new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(html, true);

        mailSender.send(message);
    }


    private void saveEmailLog(Proposal proposal,
                              ProposalEmailType type,
                              String subject) {

        ProposalEmailLog log = ProposalEmailLog.builder()
                .proposal(proposal)
                .recipient(proposal.getCustomerEmail())
                .subject(subject)
                .emailType(type)
                .sentAt(LocalDateTime.now())
                .build();

        emailLogRepository.save(log);
    }


    private String buildProposalEmail(
            String customerName,
            String proposalNumber,
            String proposalLink) {

        return """
        <html>

        <body style="font-family:Arial">

        <h2>Insurance Proposal</h2>

        <p>Dear %s,</p>

        <p>
        Your insurance proposal is ready.
        Please click below to complete it.
        </p>

        <br>

        <a href="%s"
           style="
           background:#1976d2;
           color:white;
           padding:12px 25px;
           border-radius:5px;
           text-decoration:none;
           font-weight:bold;">

            Complete Proposal

        </a>

        <br><br>

        <b>Proposal Number:</b>

        %s

        <br><br>

        <p>This link expires in 7 days.</p>

        </body>

        </html>
        """
                .formatted(
                        customerName,
                        proposalLink,
                        proposalNumber
                );
    }

    private String buildCustomerCopyEmail(
            String customerName,
            String proposalNumber) {

        return """
        <html>

        <body style="font-family:Arial">

        <h2>Proposal Submitted Successfully</h2>

        <p>Dear %s,</p>

        <p>

        Thank you for completing your proposal.

        </p>

        <p>

        Proposal Number:
        <b>%s</b>

        </p>

        <br>

        <p>

        Please keep this email for your records.

        </p>

        </body>

        </html>
        """
                .formatted(
                        customerName,
                        proposalNumber
                );
    }
}