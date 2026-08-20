package com.example.Insurance.entities.qty;


import com.example.Insurance.utils.types.CurrencyType;
import com.example.Insurance.utils.types.QuotationStatus;
import com.example.Insurance.utils.types.QuotationType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "quotations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Quotation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String quotationReference;

    @Column(nullable = false)
    private String customerName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CurrencyType currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuotationType quotationType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuotationStatus status;

    @Column(precision = 18, scale = 2)
    private BigDecimal totalSumInsured;

    @Column(precision = 18, scale = 2)
    private BigDecimal netPremium;

    @Column(precision = 18, scale = 2)
    private BigDecimal taxAndLeviesAmount;

    @Column(precision = 18, scale = 2)
    private BigDecimal grandTotalPremium;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @OneToMany(
            mappedBy = "quotation",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<AbstractRisk> risks = new ArrayList<>();

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
        status = QuotationStatus.DRAFT;
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
