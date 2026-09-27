package com.example.ClaimInsurance.entities.policy;

import com.example.ClaimInsurance.entities.User;
import com.example.ClaimInsurance.entities.customer.Customer;
import com.example.ClaimInsurance.entities.fileTrasfer.Branch;
import com.example.ClaimInsurance.entities.propsal.Proposal;
import com.example.ClaimInsurance.entities.qty.Quotation;
import com.example.Insurance.utils.types.*;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "policies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Policy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 40)
    private String policyNumber;

    @Column(nullable = false)
    @Builder.Default
    private Integer policyVersion = 1;

    @Column(length = 50)
    private String endorsementNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quotation_id")
    private Quotation quotation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proposal_id")
    private Proposal proposal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_code", referencedColumnName = "branchCode")
    private Branch branch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issued_by", referencedColumnName = "etf_no")
    private User issuedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "servicing_agent", referencedColumnName = "etf_no")
    private User servicingAgent;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PolicyType policyType;

    @Column(length = 150)
    private String productName;

    @Column(length = 50)
    private String productCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PolicyStatus status;

    @Column(nullable = false)
    private LocalDate inceptionDate;

    @Column(nullable = false)
    private LocalDate expiryDate;

    private LocalDate issueDate;

    private LocalDate renewalDate;

    private LocalDate cancellationDate;

    @Builder.Default
    private Integer termMonths = 12;

    @Column(columnDefinition = "TEXT")
    private String riskLocation;

    @Enumerated(EnumType.STRING)
    private OccupancyType occupancyType;

    @Enumerated(EnumType.STRING)
    private ConstructionType constructionType;

    private Integer yearBuilt;

    @Column(length = 100)
    private String insuredName;

    @Column(length = 20)
    private String insuredNic;

    @Column(length = 150)
    private String insuredEmail;

    @Column(length = 20)
    private String insuredPhone;

    @Column(columnDefinition = "TEXT")
    private String insuredAddress;

    @Column(length = 150)
    private String nomineeName;

    @Column(length = 100)
    private String nomineeRelationship;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CurrencyType currency;

    @Column(precision = 18, scale = 2)
    private BigDecimal buildingSumInsured;

    @Column(precision = 18, scale = 2)
    private BigDecimal contentsSumInsured;

    @Column(precision = 18, scale = 2)
    private BigDecimal totalSumInsured;

    @Column(precision = 18, scale = 2)
    private BigDecimal netPremium;

    @Column(precision = 18, scale = 2)
    private BigDecimal taxAndLeviesAmount;

    @Column(precision = 18, scale = 2)
    private BigDecimal stampDuty;

    @Column(precision = 18, scale = 2)
    private BigDecimal commissionAmount;

    @Column(precision = 18, scale = 2)
    private BigDecimal grandTotalPremium;

    @Column(precision = 18, scale = 2)
    private BigDecimal deductible;

    @Column(precision = 18, scale = 2)
    private BigDecimal excessAmount;

    @Enumerated(EnumType.STRING)
    private PaymentFrequency paymentFrequency;

    @Enumerated(EnumType.STRING)
    private PremiumPaymentStatus premiumPaymentStatus;

    @Column(precision = 18, scale = 2)
    private BigDecimal premiumPaid;

    @Column(precision = 18, scale = 2)
    private BigDecimal outstandingPremium;

    private LocalDate nextPremiumDueDate;

    @Column(length = 50)
    private String paymentMethod;

    @Column(columnDefinition = "TEXT")
    private String cancellationReason;

    @Column(columnDefinition = "TEXT")
    private String lapseReason;

    @Builder.Default
    private Boolean renewable = true;

    private Long previousPolicyId;

    @Builder.Default
    private Integer renewalCount = 0;

    @Column(columnDefinition = "TEXT")
    private String specialConditions;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @OneToMany(
            mappedBy = "policy",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<PolicyCoverage> coverages = new ArrayList<>();

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = PolicyStatus.DRAFT;
        }
        if (policyVersion == null) {
            policyVersion = 1;
        }
        if (renewalCount == null) {
            renewalCount = 0;
        }
        if (renewable == null) {
            renewable = true;
        }
        if (premiumPaymentStatus == null) {
            premiumPaymentStatus = PremiumPaymentStatus.UNPAID;
        }
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
