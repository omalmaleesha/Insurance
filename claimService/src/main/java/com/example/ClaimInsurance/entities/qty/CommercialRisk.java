package com.example.ClaimInsurance.entities.qty;


import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "commercial_risks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommercialRisk extends AbstractRisk {

    private String businessActivity;

    private String footTrafficLevel;

    private Boolean serverRoom;

    private Boolean kitchenOrCookingFacility;

    private Boolean hoseReels;

}
