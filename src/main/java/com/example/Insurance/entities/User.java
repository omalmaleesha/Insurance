package com.example.Insurance.entities;

import com.example.Insurance.utils.EmployeeType;
import com.example.Insurance.utils.Gender;
import com.example.Insurance.utils.Status;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @Column(name = "etf_no", length = 20)
    private String etfNo;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(unique = true, length = 20)
    private String nic;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(nullable = false, length = 100)
    private String designation;

    @Enumerated(EnumType.STRING)
    @Column(name = "employee_type")
    private EmployeeType employeeType;

    @Column(length = 100)
    private String specialization;

    @Column(name = "underwriting_limit", precision = 15, scale = 2)
    private BigDecimal underwritingLimit;

    @Column(name = "approval_level")
    private Integer approvalLevel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_code", referencedColumnName = "branchCode")
    private Branch branch;

    @Column(nullable = false, length = 100)
    private String department;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "joined_date")
    private LocalDate joinedDate;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public String getBranchCode(User user) {
        return user.getBranchCode(user);
    }
}