package com.example.Insurance.dto.qty;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResidentialRiskRequest {

    private String propertyName;

    private String locationAddress;

    private BigDecimal buildingSumInsured;

    private BigDecimal contentsSumInsured;

    private Boolean hasFireAlarm;

    private Boolean hasAutomaticSprinklers;

    private Double distanceToNearestFireStationKm;

    private String constructionMaterialClass;

    private Integer numberOfFloors;

    private Integer yearBuilt;

    private String occupancyType;

    private Boolean isSecuredGatedProperty;

    public BigDecimal getLoadingPercentage() {
        return null;
    }

    public BigDecimal getDiscountPercentage() {
        return null;
    }

}