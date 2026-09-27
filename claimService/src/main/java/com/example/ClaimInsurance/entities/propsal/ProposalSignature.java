package com.example.ClaimInsurance.entities.propsal;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "proposal_signatures")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProposalSignature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "proposal_id")
    private Proposal proposal;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String signatureBase64;

    private String signedBy;

    private LocalDateTime signedAt;

}