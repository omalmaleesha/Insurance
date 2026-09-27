package com.example.ClaimInsurance.entities.policy;

import com.example.Insurance.utils.types.CoverType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "policy_coverages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PolicyCoverage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", nullable = false)
    private Policy policy;

    @Column(length = 50)
    private String coverCode;

    @Column(nullable = false, length = 150)
    private String coverName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CoverType coverType;

    @Column(precision = 18, scale = 2)
    private BigDecimal sumInsured;

    @Column(precision = 8, scale = 4)
    private BigDecimal rate;

    @Column(precision = 18, scale = 2)
    private BigDecimal premium;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Builder.Default
    private Boolean optional = false;
}
