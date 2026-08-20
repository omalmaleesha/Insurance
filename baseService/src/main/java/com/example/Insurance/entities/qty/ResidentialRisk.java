package com.example.Insurance.entities.qty;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

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
