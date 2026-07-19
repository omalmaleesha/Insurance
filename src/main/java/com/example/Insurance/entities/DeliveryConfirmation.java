package com.example.Insurance.entities;

import com.example.Insurance.utils.DeliveryCondition;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
@Entity
@Table(name = "delivery_confirmations")
public class DeliveryConfirmation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "shipment_id")
    private CourierShipment shipment;

    @ManyToOne
    @JoinColumn(name = "received_by")
    private User receivedBy;

    private LocalDateTime receivedDate;

    @Enumerated(EnumType.STRING)
    private DeliveryCondition condition;

    private String remarks;
}