package com.example.Insurance.dto.auth;

import com.example.Insurance.utils.types.EmployeeType;
import com.example.Insurance.utils.types.Gender;
import com.example.Insurance.utils.types.Role;
import com.example.Insurance.utils.types.Status;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {

    private String etfNo;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phoneNumber;
    private String nic;
    private Gender gender;
    private LocalDate dateOfBirth;
    private String address;
    private String designation;
    private EmployeeType employeeType;
    private String specialization;
    private BigDecimal underwritingLimit;
    private Integer approvalLevel;
    private String branchCode;
    private String department;
    private Status status;
    private LocalDate joinedDate;
    private Role role;
}