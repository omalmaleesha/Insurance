package com.example.Insurance.dto.customer;

import lombok.Data;

@Data
public class CorporateCustomerCreateRequestDTO {

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
}
