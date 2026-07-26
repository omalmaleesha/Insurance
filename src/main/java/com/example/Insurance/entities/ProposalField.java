package com.example.Insurance.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "proposal_fields")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProposalField {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "proposal_id")
    private Proposal proposal;

    private String fieldName;

    @Lob
    private String fieldValue;
}