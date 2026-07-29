package com.example.Insurance.dto.qty;

import java.math.BigDecimal;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IndustrialRiskRequest {

    private String propertyName;

    private String locationAddress;

    private BigDecimal buildingSumInsured;

    private BigDecimal contentsSumInsured;

    private Boolean hasFireAlarm;

    private Boolean hasAutomaticSprinklers;

    private Double distanceToNearestFireStationKm;

    private String manufacturingType;

    private BigDecimal machinerySumInsured;

    private Boolean hasFlammableMaterials;

    private String flammableMaterialCategory;

    private Boolean hasBoilersOrHeavyMachinery;

    private Boolean hasOnsiteHydrants;

    private Boolean hasFireDoorsAndWalls;

}
