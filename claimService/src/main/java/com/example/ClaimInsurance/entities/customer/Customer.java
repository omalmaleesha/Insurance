package com.example.ClaimInsurance.entities.customer;

import com.example.ClaimInsurance.utils.types.CustomerType;
import com.example.ClaimInsurance.utils.types.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "customers")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
public abstract class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String customerCode;

    @Column(nullable = false)
    private String email;

    private String phoneNumber;

    @Column(columnDefinition = "TEXT")
    private String address;

    private String city;

    private String country;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CustomerType customerType;

    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}