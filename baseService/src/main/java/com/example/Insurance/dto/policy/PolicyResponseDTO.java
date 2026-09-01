package com.example.Insurance.dto.policy;

import com.example.Insurance.utils.types.ConstructionType;
import com.example.Insurance.utils.types.CurrencyType;
import com.example.Insurance.utils.types.OccupancyType;
import com.example.Insurance.utils.types.PaymentFrequency;
import com.example.Insurance.utils.types.PolicyStatus;
import com.example.Insurance.utils.types.PolicyType;
import com.example.Insurance.utils.types.PremiumPaymentStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class PolicyResponseDTO {

    private Long id;

    private String policyNumber;

    private Integer policyVersion;

    private String endorsementNumber;

    private Long customerId;

    private String customerCode;

    private Long quotationId;

    private String quotationReference;

    private Long proposalId;

    private String proposalNumber;

    private String branchCode;

    private String branchName;

    private String issuedByEtfNo;

    private String servicingAgentEtfNo;

    private PolicyType policyType;

    private String productName;

    private String productCode;

    private PolicyStatus status;

    private LocalDate inceptionDate;

    private LocalDate expiryDate;

    private LocalDate issueDate;

    private LocalDate renewalDate;

    private LocalDate cancellationDate;

    private Integer termMonths;

    private String riskLocation;

    private OccupancyType occupancyType;

    private ConstructionType constructionType;

    private Integer yearBuilt;

    private String insuredName;

    private String insuredNic;

    private String insuredEmail;

    private String insuredPhone;

    private String insuredAddress;

    private String nomineeName;

    private String nomineeRelationship;

    private CurrencyType currency;

    private BigDecimal buildingSumInsured;

    private BigDecimal contentsSumInsured;

    private BigDecimal totalSumInsured;

    private BigDecimal netPremium;

    private BigDecimal taxAndLeviesAmount;

    private BigDecimal stampDuty;

    private BigDecimal commissionAmount;

    private BigDecimal grandTotalPremium;

    private BigDecimal deductible;

    private BigDecimal excessAmount;

    private PaymentFrequency paymentFrequency;

    private PremiumPaymentStatus premiumPaymentStatus;

    private BigDecimal premiumPaid;

    private BigDecimal outstandingPremium;

    private LocalDate nextPremiumDueDate;

    private String paymentMethod;

    private String cancellationReason;

    private String lapseReason;

    private Boolean renewable;

    private Long previousPolicyId;

    private Integer renewalCount;

    private String specialConditions;

    private String remarks;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private List<PolicyCoverageDTO> coverages = new ArrayList<>();
}
