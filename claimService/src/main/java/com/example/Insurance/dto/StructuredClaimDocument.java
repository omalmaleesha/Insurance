package com.example.Insurance.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record StructuredClaimDocument(
        String claimNumber,
        String policyNumber,
        String customerName,
        String customerCode,
        String nic,
        String businessName,
        String businessRegistrationNumber,
        String premisesAddress,
        LocalDate incidentDate,
        LocalTime incidentTime,
        String incidentLocation,
        String causeOfLoss,
        String incidentDescription,
        String buildingDamage,
        String contentsDamage,
        BigDecimal estimatedBuildingLoss,
        BigDecimal estimatedContentsLoss,
        BigDecimal estimatedTotalLoss,
        String authorityName,
        String authorityReference,
        String otherInsurance,
        String previousClaim
) {
}