package com.example.Insurance.entities.propsal;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "proposal_access_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProposalAccessToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "proposal_id")
    private Proposal proposal;

    @Column(unique = true)
    private String token;

    private LocalDateTime expiryDate;

    private boolean used;
}