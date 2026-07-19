package com.example.Insurance.dto.shipment;

import com.example.Insurance.utils.DeliveryCondition;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReceiveShipmentRequest {

    private DeliveryCondition condition;

    private String remarks;

}