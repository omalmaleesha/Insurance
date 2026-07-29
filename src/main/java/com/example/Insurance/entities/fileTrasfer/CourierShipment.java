package com.example.Insurance.entities.fileTrasfer;

import com.example.Insurance.entities.User;
import com.example.Insurance.utils.types.ShipmentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "courier_shipments")
public class CourierShipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String trackingNumber;

    @ManyToOne
    @JoinColumn(name = "from_branch_id")
    private Branch fromBranch;

    @ManyToOne
    @JoinColumn(name = "to_branch_id")
    private Branch toBranch;

    @ManyToOne
    @JoinColumn(name = "sent_by")
    private User sentBy;

    private LocalDateTime sentDate;

    private String courierCompany;

    private String courierReference;

    @Enumerated(EnumType.STRING)
    private ShipmentStatus status;

    private String remarks;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}