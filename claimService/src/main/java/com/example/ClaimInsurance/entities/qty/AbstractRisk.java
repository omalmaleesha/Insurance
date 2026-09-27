package com.example.ClaimInsurance.entities.qty;


import com.example.Insurance.utils.types.RiskEntityType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "quotation_risks")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class AbstractRisk {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quotation_id", nullable = false)
    private Quotation quotation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RiskEntityType entityType;

    @Column(nullable = false)
    private String propertyName;

    @Column(nullable = false)
    private String locationAddress;

    @Column(precision = 18, scale = 2)
    private BigDecimal buildingSumInsured;

    @Column(precision = 18, scale = 2)
    private BigDecimal contentsSumInsured;

    @Column(precision = 18, scale = 2)
    private BigDecimal totalSumInsured;

    @Column(precision = 8, scale = 4)
    private BigDecimal baseRatePerThousand;

    @Column(precision = 8, scale = 2)
    private BigDecimal loadingPercentage;

    @Column(precision = 8, scale = 2)
    private BigDecimal discountPercentage;

    @Column(precision = 18, scale = 2)
    private BigDecimal calculatedPremium;

    private Boolean hasFireAlarm;

    private Boolean hasAutomaticSprinklers;

    private Double distanceToNearestFireStationKm;

}
