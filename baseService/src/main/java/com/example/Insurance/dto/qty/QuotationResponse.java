package com.example.Insurance.dto.qty;

import com.example.Insurance.utils.types.CurrencyType;
import com.example.Insurance.utils.types.QuotationStatus;
import com.example.Insurance.utils.types.QuotationType;

import java.math.BigDecimal;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuotationResponse {

    private Long id;

    private String quotationReference;

    private String customerName;

    private CurrencyType currency;

    private QuotationType quotationType;

    private QuotationStatus status;

    private BigDecimal totalSumInsured;

    private BigDecimal netPremium;

    private BigDecimal taxAndLeviesAmount;

    private BigDecimal grandTotalPremium;

}