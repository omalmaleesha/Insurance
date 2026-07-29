package com.example.Insurance.entities.propsal;

import com.example.Insurance.utils.types.ProposalEmailType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "proposal_email_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProposalEmailLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String recipient;

    private String subject;

    @Enumerated(EnumType.STRING)
    private ProposalEmailType emailType;

    private LocalDateTime sentAt;

    @ManyToOne
    @JoinColumn(name = "proposal_id")
    private Proposal proposal;
}
