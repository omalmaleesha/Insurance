package com.example.Insurance.controllers;

import com.example.Insurance.dto.AuthRequest;
import com.example.Insurance.dto.UserDTO;
import com.example.Insurance.entities.Branch;
import com.example.Insurance.entities.User;
import com.example.Insurance.repository.BranchRepository;
import com.example.Insurance.utils.JwtService;
import com.example.Insurance.utils.UserInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserInfoService service;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final BranchRepository branchRepository;

    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome this endpoint is not secure";
    }




    @PostMapping("/addNewUser")
    public String addNewUser(@RequestBody UserDTO userInfo) {
        System.out.println("Adding new user");
        System.out.println(userInfo);
        Branch branch = branchRepository
                .findByBranchCode(userInfo.getBranchCode())
                .orElseThrow(() -> new RuntimeException("Branch not found"));

        User user = User.builder()
                .email(userInfo.getEmail())
                .password(userInfo.getPassword())
                .firstName(userInfo.getFirstName())
                .lastName(userInfo.getLastName())
                .role(userInfo.getRole())
                .etfNo(userInfo.getEtfNo())
                .phoneNumber(userInfo.getPhoneNumber())
                .nic(userInfo.getNic())
                .gender(userInfo.getGender())
                .dateOfBirth(userInfo.getDateOfBirth())
                .address(userInfo.getAddress())
                .designation(userInfo.getDesignation())
                .department(userInfo.getDepartment())
                .employeeType(userInfo.getEmployeeType())
                .specialization(userInfo.getSpecialization())
                .underwritingLimit(userInfo.getUnderwritingLimit())
                .approvalLevel(userInfo.getApprovalLevel())
                .branch(branch)
                .build();

        return service.addUser(user);
    }

    @PostMapping("/generateToken")
    public String authenticateAndGetToken(@RequestBody AuthRequest authRequest) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                authRequest.getUsername(),
                                authRequest.getPassword()));

        if (authentication.isAuthenticated()) {
            return jwtService.generateToken(authRequest.getUsername());
        }

        throw new UsernameNotFoundException("Invalid user request!");
    }
}