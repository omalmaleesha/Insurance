package com.example.Insurance.dto.customer;


import com.example.Insurance.utils.types.Status;
import lombok.Data;

@Data
public class CustomerUpdateRequestDTO {

    private String email;

    private String phoneNumber;

    private String address;

    private String city;

    private String country;

    private Status status;
}