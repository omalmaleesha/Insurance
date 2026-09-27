package com.example.ClaimInsurance.entities.propsal;

import com.example.ClaimInsurance.entities.User;
import com.example.Insurance.utils.types.ProposalStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "proposals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Proposal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String proposalNumber;

    private String customerName;

    private String customerEmail;

    private String customerPhone;

    private String productName;

    @Enumerated(EnumType.STRING)
    private ProposalStatus status;

    private LocalDateTime sentAt;

    private LocalDateTime openedAt;

    private LocalDateTime submittedAt;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    @OneToOne(mappedBy = "proposal",
            cascade = CascadeType.ALL)
    private ProposalSignature signature;
}