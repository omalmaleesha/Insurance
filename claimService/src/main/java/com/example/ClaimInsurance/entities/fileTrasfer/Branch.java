package com.example.ClaimInsurance.entities.fileTrasfer;

import com.example.Insurance.utils.types.BranchType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "branches")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Branch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String branchCode;

    @Getter
    @Column(nullable = false)
    private String branchName;

    @Enumerated(EnumType.STRING)
    private BranchType branchType;

    private String address;

    private String contactNumber;

    private String email;

    private Boolean active = true;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}