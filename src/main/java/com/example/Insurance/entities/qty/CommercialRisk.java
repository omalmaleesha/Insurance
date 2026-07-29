package com.example.Insurance.entities.qty;


import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

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
