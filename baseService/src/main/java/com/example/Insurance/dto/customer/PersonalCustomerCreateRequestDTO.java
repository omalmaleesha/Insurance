package com.example.Insurance.dto.customer;

import com.example.Insurance.utils.types.Gender;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PersonalCustomerCreateRequestDTO {

    private String customerCode;

    private String firstName;

    private String lastName;

    private String nic;

    private LocalDate dateOfBirth;

    private Gender gender;

    private String occupation;

    private String email;

    private String phoneNumber;

    private String address;

    private String city;

    private String country;
}
