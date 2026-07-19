package com.example.Insurance.dto.shipmentitem;

import com.example.Insurance.utils.DocumentType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentItemRequest {

    private DocumentType documentType;

    private String documentNumber;

    private String customerName;

    private String policyNumber;

    private Integer pageCount;

    private String remarks;

}
