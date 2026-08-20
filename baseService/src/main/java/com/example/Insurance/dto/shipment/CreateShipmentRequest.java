package com.example.Insurance.dto.shipment;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateShipmentRequest {

    private Long fromBranchId;
    private Long toBranchId;

    private String courierCompany;
    private String courierReference;

    private String remarks;

}
