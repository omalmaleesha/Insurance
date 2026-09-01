package com.example.Insurance.controllers;


import com.example.Insurance.dto.ClaimDTO;

import com.example.Insurance.service.ClaimService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claims")
@RequiredArgsConstructor
public class ClaimController {
    //need notifies - agent,branch,customer must when create a new claim

    private final ClaimService claimService;

    // CREATE CLAIM
    // Customer calls hotline -> staff creates claim
    @PostMapping
    public ResponseEntity<ClaimDTO> createClaim(
            @RequestBody ClaimDTO claimDTO
    ) {

        ClaimDTO createdClaim =
                claimService.createClaim(claimDTO);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdClaim);
    }


    // GET ALL CLAIMS
    @GetMapping
    public ResponseEntity<List<ClaimDTO>> getAllClaims() {

        return ResponseEntity.ok(
                claimService.getAllClaims()
        );
    }


    // GET CLAIM BY ID
    @GetMapping("/{id}")
    public ResponseEntity<ClaimDTO> getClaimById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                claimService.getClaimById(id)
        );
    }


    // GET CLAIM BY CLAIM NUMBER
    @GetMapping("/number/{claimNumber}")
    public ResponseEntity<ClaimDTO> getClaimByNumber(
            @PathVariable String claimNumber
    ) {

        return ResponseEntity.ok(
                claimService.getClaimByNumber(claimNumber)
        );
    }


    // GET CLAIMS BY CUSTOMER ID
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<ClaimDTO>> getClaimsByCustomer(
            @PathVariable Long customerId
    ) {

        return ResponseEntity.ok(
                claimService.getClaimsByCustomerId(customerId)
        );
    }


    // UPDATE CLAIM
    @PutMapping("/{id}")
    public ResponseEntity<ClaimDTO> updateClaim(
            @PathVariable Long id,
            @RequestBody ClaimDTO claimDTO
    ) {

        ClaimDTO updatedClaim =
                claimService.updateClaim(id, claimDTO);

        return ResponseEntity.ok(updatedClaim);
    }


    // DELETE CLAIM
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClaim(
            @PathVariable Long id
    ) {

        claimService.deleteClaim(id);

        return ResponseEntity.noContent().build();
    }
}

//{
//        "customerId": 1,
//        "createdByEtfNo": "ETF001",
//        "claimType": "MOTOR",
//        "incidentDate": "2026-08-23T10:30:00",
//        "incidentLocation": "Colombo",
//        "incidentDescription": "Vehicle was damaged in an accident.",
//        "situationStatement": "Another vehicle collided with the customer's vehicle.",
//        "branchCode": "COL001"
//}