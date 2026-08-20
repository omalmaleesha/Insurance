package com.example.Insurance.services.impl;

import com.example.Insurance.entities.qty.AbstractRisk;
import com.example.Insurance.entities.qty.Quotation;
import com.example.Insurance.services.PremiumCalculationService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class PremiumCalculationServiceImpl
        implements PremiumCalculationService {

    private static final BigDecimal THOUSAND =
            new BigDecimal("1000");

    // VAT + Levies (change when required)
    private static final BigDecimal TAX_PERCENT =
            new BigDecimal("18");

    @Override
    public void calculateQuotation(
            Quotation quotation,
            AbstractRisk risk) {

        calculateTotalSumInsured(risk);

        calculateBaseRate(risk);

        calculatePremium(risk);

        calculateTaxAndLevies(quotation, risk);

    }

    /**
     * Total Sum Insured
     */
    private void calculateTotalSumInsured(
            AbstractRisk risk) {

        BigDecimal building =
                value(risk.getBuildingSumInsured());

        BigDecimal contents =
                value(risk.getContentsSumInsured());

        risk.setTotalSumInsured(
                building.add(contents)
        );

    }

    /**
     * Decide the base rate
     */
    private void calculateBaseRate(
            AbstractRisk risk) {

        BigDecimal rate =
                new BigDecimal("1.50");

        if (Boolean.TRUE.equals(risk.getHasFireAlarm())) {

            rate = rate.subtract(
                    new BigDecimal("0.10"));

        }

        if (Boolean.TRUE.equals(
                risk.getHasAutomaticSprinklers())) {

            rate = rate.subtract(
                    new BigDecimal("0.20"));

        }

        if (risk.getDistanceToNearestFireStationKm() != null
                && risk.getDistanceToNearestFireStationKm() > 5) {

            rate = rate.add(
                    new BigDecimal("0.25"));

        }

        risk.setBaseRatePerThousand(rate);

    }

    /**
     * Premium calculation
     */
    private void calculatePremium(
            AbstractRisk risk) {

        BigDecimal sumInsured =
                value(risk.getTotalSumInsured());

        BigDecimal rate =
                value(risk.getBaseRatePerThousand());

        BigDecimal premium =
                sumInsured
                        .divide(THOUSAND, 2, RoundingMode.HALF_UP)
                        .multiply(rate);

        BigDecimal loading =
                premium.multiply(
                        value(risk.getLoadingPercentage())
                                .divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP)
                );

        BigDecimal discount =
                premium.multiply(
                        value(risk.getDiscountPercentage())
                                .divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP)
                );

        premium =
                premium
                        .add(loading)
                        .subtract(discount);

        risk.setCalculatedPremium(
                premium.setScale(2, RoundingMode.HALF_UP));

    }

    /**
     * Grand Totals
     */
    private void calculateTaxAndLevies(
            Quotation quotation,
            AbstractRisk risk) {

        BigDecimal premium =
                value(risk.getCalculatedPremium());

        BigDecimal tax =
                premium.multiply(TAX_PERCENT)
                        .divide(new BigDecimal("100"),
                                2,
                                RoundingMode.HALF_UP);

        BigDecimal grand =
                premium.add(tax);

        quotation.setTotalSumInsured(
                risk.getTotalSumInsured());

        quotation.setNetPremium(
                premium);

        quotation.setTaxAndLeviesAmount(
                tax);

        quotation.setGrandTotalPremium(
                grand);

    }

    private BigDecimal value(BigDecimal value) {

        return value == null
                ? BigDecimal.ZERO
                : value;

    }

}