package com.example.Insurance.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "corporate_customers")
@Getter
@Setter
public class CorporateCustomer extends Customer {

    @Column(nullable = false)
    private String companyName;

    @Column(unique = true)
    private String registrationNumber;

    private String industry;

    private String contactPerson;

    private String contactDesignation;

    private String contactPhone;

    private String website;
}