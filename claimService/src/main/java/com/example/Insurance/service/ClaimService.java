package com.example.Insurance.service;

import com.example.Insurance.dto.ClaimDTO;

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
