package com.example.Insurance.entities.qty;


import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

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
