package com.example.Insurance.dto.shipment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DispatchShipmentRequest {

    private String courierCompany;

    private String courierReference;

}
