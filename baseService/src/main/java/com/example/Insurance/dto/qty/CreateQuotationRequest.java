package com.example.Insurance.dto.qty;

import com.example.Insurance.utils.types.CurrencyType;
import com.example.Insurance.utils.types.QuotationType;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateQuotationRequest {

    private String customerName;

    private CurrencyType currency;

    private QuotationType quotationType;

    private ResidentialRiskRequest residentialRisk;

    private CommercialRiskRequest commercialRisk;

    private IndustrialRiskRequest industrialRisk;

}