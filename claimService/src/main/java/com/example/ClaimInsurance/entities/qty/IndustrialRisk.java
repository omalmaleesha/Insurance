package com.example.ClaimInsurance.entities.qty;


import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "industrial_risks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IndustrialRisk extends AbstractRisk {

    private String manufacturingType;

    private BigDecimal machinerySumInsured;

    private Boolean flammableMaterials;

    private String flammableMaterialCategory;

    private Boolean boilersOrHeavyMachinery;

    private Boolean onsiteHydrants;

    private Boolean fireDoorsAndWalls;

}
