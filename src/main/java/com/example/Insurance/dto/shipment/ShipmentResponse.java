package com.example.Insurance.dto.shipment;

import com.example.Insurance.utils.ShipmentStatus;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShipmentResponse {

    private Long id;

    private String trackingNumber;

    private String fromBranch;

    private String toBranch;

    private String sentBy;

    private LocalDateTime sentDate;

    private String courierCompany;

    private String courierReference;

    private ShipmentStatus status;

    private String remarks;

}
