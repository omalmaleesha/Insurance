package com.example.Insurance.dto.policy;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PolicyRenewRequestDTO {

    private LocalDate inceptionDate;

    private LocalDate expiryDate;

    private Integer termMonths;

    private BigDecimal netPremium;

    private BigDecimal taxAndLeviesAmount;

    private BigDecimal stampDuty;

    private BigDecimal commissionAmount;

    private BigDecimal grandTotalPremium;

    private BigDecimal totalSumInsured;
}
