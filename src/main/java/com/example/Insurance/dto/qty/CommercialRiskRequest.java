package com.example.Insurance.dto.qty;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommercialRiskRequest {

    private String propertyName;

    private String locationAddress;

    private BigDecimal buildingSumInsured;

    private BigDecimal contentsSumInsured;

    private Boolean hasFireAlarm;

    private Boolean hasAutomaticSprinklers;

    private Double distanceToNearestFireStationKm;

    private String businessActivity;

    private String footTrafficLevel;

    private Boolean hasServerRoom;

    private Boolean hasKitchenOrCookingFacility;

    private Boolean hasHoseReels;

}
