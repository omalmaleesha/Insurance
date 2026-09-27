package com.example.ClaimInsurance.entities.qty;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "residential_risks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResidentialRisk extends AbstractRisk {

    private String constructionMaterialClass;

    private Integer numberOfFloors;

    private Integer yearBuilt;

    private String occupancyType;

    private Boolean securedGatedProperty;

}
