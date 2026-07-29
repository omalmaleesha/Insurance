package com.example.Insurance.dto.shipmentitem;
import com.example.Insurance.utils.types.DocumentType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShipmentItemResponse {

    private Long id;

    private DocumentType documentType;

    private String documentNumber;

    private String customerName;

    private String policyNumber;

    private Integer pageCount;

    private Boolean received;

}