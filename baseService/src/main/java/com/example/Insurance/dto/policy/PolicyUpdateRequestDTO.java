package com.example.Insurance.dto.policy;

import com.example.Insurance.utils.types.ConstructionType;
import com.example.Insurance.utils.types.CurrencyType;
import com.example.Insurance.utils.types.OccupancyType;
import com.example.Insurance.utils.types.PaymentFrequency;
import com.example.Insurance.utils.types.PolicyType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class PolicyUpdateRequestDTO {

    private String branchCode;

    private String servicingAgentEtfNo;

    private PolicyType policyType;

    private String productName;

    private String productCode;

    private LocalDate inceptionDate;

    private LocalDate expiryDate;

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

    private LocalDate nextPremiumDueDate;

    private String paymentMethod;

    private Boolean renewable;

    private String specialConditions;

    private String remarks;

    private List<PolicyCoverageDTO> coverages = new ArrayList<>();
}
