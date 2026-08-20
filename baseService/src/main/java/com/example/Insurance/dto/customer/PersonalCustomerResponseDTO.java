package com.example.Insurance.dto.customer;

import com.example.Insurance.utils.types.Gender;
import com.example.Insurance.utils.types.Status;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PersonalCustomerResponseDTO {

    private Long id;

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

    private Status status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
