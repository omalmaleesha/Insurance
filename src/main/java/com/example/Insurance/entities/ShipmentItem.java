package com.example.Insurance.entities;
import com.example.Insurance.utils.DocumentType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
@Entity
@Table(name = "shipment_items")
public class ShipmentItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "shipment_id")
    private CourierShipment shipment;

    @Enumerated(EnumType.STRING)
    private DocumentType documentType;

    private String documentNumber;

    private String customerName;

    private String policyNumber;

    private Integer pageCount;

    private String remarks;

    private Boolean received = false;

    private LocalDateTime receivedDate;
}
