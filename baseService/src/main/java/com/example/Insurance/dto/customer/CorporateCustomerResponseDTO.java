package com.example.Insurance.dto.customer;

import com.example.Insurance.utils.types.Status;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CorporateCustomerResponseDTO {

    private Long id;

    private String customerCode;

    private String companyName;

    private String registrationNumber;

    private String industry;

    private String contactPerson;

    private String contactDesignation;

    private String contactPhone;

    private String website;

    private String email;

    private String phoneNumber;

    private String address;

    private String city;

    private String country;

    private Status status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
