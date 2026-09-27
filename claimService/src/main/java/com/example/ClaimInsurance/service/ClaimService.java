package com.example.ClaimInsurance.service;

import com.example.ClaimInsurance.dto.ClaimDTO;

import java.util.List;

public interface ClaimService {
    ClaimDTO createClaim(ClaimDTO claimDTO);
    List<ClaimDTO> getAllClaims();
    ClaimDTO getClaimById(Long id);
    ClaimDTO getClaimByNumber(String claimNumber);
    List<ClaimDTO> getClaimsByCustomerId(Long customerId);
    ClaimDTO updateClaim(Long id, ClaimDTO claimDTO);
    void deleteClaim(Long id);
}
