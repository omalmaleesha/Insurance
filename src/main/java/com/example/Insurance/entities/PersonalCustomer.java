package com.example.Insurance.entities;


import com.example.Insurance.utils.types.Gender;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "personal_customers")
@Getter
@Setter
public class PersonalCustomer extends Customer {

    private String firstName;

    private String lastName;

    @Column(unique = true)
    private String nic;

    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String occupation;
}