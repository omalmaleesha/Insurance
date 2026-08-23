package com.example.Insurance.service.impl;

import com.example.Insurance.dto.ClaimDTO;
import com.example.Insurance.entities.Claim;
import com.example.Insurance.repository.ClaimRepository;
import com.example.Insurance.service.ClaimService;
import com.example.Insurance.utils.types.ClaimStatus;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ClaimServiceImpl implements ClaimService {

    private final ClaimRepository claimRepository;


    // =========================================================
    // CREATE CLAIM
    // Customer calls hotline -> Staff creates a claim
    // =========================================================
    @Override
    public ClaimDTO createClaim(ClaimDTO claimDTO) {

        validateCreateRequest(claimDTO);

        Claim claim = Claim.builder()
                .claimNumber(generateClaimNumber())
                .customerId(claimDTO.getCustomerId())
                .createdByEtfNo(claimDTO.getCreatedByEtfNo())
                .claimType(claimDTO.getClaimType())
                .status(ClaimStatus.REPORTED)
                .incidentDate(claimDTO.getIncidentDate())
                .reportedDate(LocalDateTime.now())
                .incidentLocation(
                        cleanString(claimDTO.getIncidentLocation())
                )
                .incidentDescription(
                        cleanString(claimDTO.getIncidentDescription())
                )
                .situationStatement(
                        cleanString(claimDTO.getSituationStatement())
                )
                .branchCode(
                        cleanString(claimDTO.getBranchCode())
                )
                .build();

        Claim savedClaim = claimRepository.save(claim);

        return mapToDTO(savedClaim);
    }


    // =========================================================
    // GET ALL CLAIMS
    // Read-only transaction improves performance
    // =========================================================
    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<ClaimDTO> getAllClaims() {

        return claimRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }


    // =========================================================
    // GET CLAIM BY ID
    // =========================================================
    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public ClaimDTO getClaimById(Long id) {

        validateId(id);

        Claim claim = findClaimById(id);

        return mapToDTO(claim);
    }


    // =========================================================
    // GET CLAIM BY CLAIM NUMBER
    // =========================================================
    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public ClaimDTO getClaimByNumber(String claimNumber) {

        if (claimNumber == null || claimNumber.isBlank()) {
            throw new IllegalArgumentException(
                    "Claim number must not be empty"
            );
        }

        Claim claim = claimRepository
                .findByClaimNumber(claimNumber.trim())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Claim not found: " + claimNumber
                        )
                );

        return mapToDTO(claim);
    }


    // =========================================================
    // GET CLAIMS BY CUSTOMER ID
    // =========================================================
    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<ClaimDTO> getClaimsByCustomerId(Long customerId) {

        if (customerId == null || customerId <= 0) {
            throw new IllegalArgumentException(
                    "Customer ID must be a valid positive number"
            );
        }

        return claimRepository
                .findByCustomerId(customerId)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }


    // =========================================================
    // UPDATE CLAIM
    // Only editable claim information is updated.
    //
    // claimNumber, customerId, createdByEtfNo,
    // reportedDate, createdAt are NOT changed.
    // =========================================================
    @Override
    public ClaimDTO updateClaim(Long id, ClaimDTO claimDTO) {

        validateId(id);

        if (claimDTO == null) {
            throw new IllegalArgumentException(
                    "Claim data must not be null"
            );
        }

        Claim claim = findClaimById(id);

        /*
         * Do not allow modification after final decision.
         */
        if (claim.getStatus() == ClaimStatus.APPROVED ||
                claim.getStatus() == ClaimStatus.REJECTED ||
                claim.getStatus() == ClaimStatus.PAID ||
                claim.getStatus() == ClaimStatus.CLOSED) {

            throw new IllegalStateException(
                    "Claim cannot be updated in status: "
                            + claim.getStatus()
            );
        }

        // Only update fields if a value is provided

        if (claimDTO.getClaimType() != null) {
            claim.setClaimType(claimDTO.getClaimType());
        }

        if (claimDTO.getIncidentDate() != null) {

            validateIncidentDate(
                    claimDTO.getIncidentDate()
            );

            claim.setIncidentDate(
                    claimDTO.getIncidentDate()
            );
        }

        if (claimDTO.getIncidentLocation() != null) {
            claim.setIncidentLocation(
                    cleanString(claimDTO.getIncidentLocation())
            );
        }

        if (claimDTO.getIncidentDescription() != null) {
            claim.setIncidentDescription(
                    cleanString(claimDTO.getIncidentDescription())
            );
        }

        if (claimDTO.getSituationStatement() != null) {
            claim.setSituationStatement(
                    cleanString(claimDTO.getSituationStatement())
            );
        }

        if (claimDTO.getBranchCode() != null) {
            claim.setBranchCode(
                    cleanString(claimDTO.getBranchCode())
            );
        }

        /*
         * IMPORTANT:
         *
         * Status should ideally NOT be changed using this
         * general update method.
         *
         * Later create a dedicated method such as:
         *
         * updateClaimStatus()
         *
         * This allows you to control valid workflow transitions.
         */

        Claim updatedClaim = claimRepository.save(claim);

        return mapToDTO(updatedClaim);
    }


    // =========================================================
    // DELETE CLAIM
    // =========================================================
    @Override
    public void deleteClaim(Long id) {

        validateId(id);

        Claim claim = findClaimById(id);

        /*
         * Do not allow deleting claims already processed.
         */
        if (claim.getStatus() != ClaimStatus.REPORTED) {

            throw new IllegalStateException(
                    "Only newly reported claims can be deleted. " +
                            "Current status: " +
                            claim.getStatus()
            );
        }

        claimRepository.delete(claim);
    }


    // =========================================================
    // VALIDATE CREATE REQUEST
    // =========================================================
    private void validateCreateRequest(ClaimDTO claimDTO) {

        if (claimDTO == null) {
            throw new IllegalArgumentException(
                    "Claim data must not be null"
            );
        }

        if (claimDTO.getCustomerId() == null ||
                claimDTO.getCustomerId() <= 0) {

            throw new IllegalArgumentException(
                    "Customer ID must be a valid positive number"
            );
        }

        if (claimDTO.getCreatedByEtfNo() == null ||
                claimDTO.getCreatedByEtfNo().isBlank()) {

            throw new IllegalArgumentException(
                    "Created by ETF number is required"
            );
        }

        if (claimDTO.getClaimType() == null) {

            throw new IllegalArgumentException(
                    "Claim type is required"
            );
        }

        if (claimDTO.getIncidentDate() == null) {

            throw new IllegalArgumentException(
                    "Incident date is required"
            );
        }

        validateIncidentDate(
                claimDTO.getIncidentDate()
        );

        if (isBlank(claimDTO.getIncidentLocation())) {

            throw new IllegalArgumentException(
                    "Incident location is required"
            );
        }

        if (isBlank(claimDTO.getIncidentDescription())) {

            throw new IllegalArgumentException(
                    "Incident description is required"
            );
        }

        if (isBlank(claimDTO.getBranchCode())) {

            throw new IllegalArgumentException(
                    "Branch code is required"
            );
        }
    }


    // =========================================================
    // VALIDATE INCIDENT DATE
    // Incident cannot happen in the future
    // =========================================================
    private void validateIncidentDate(
            LocalDateTime incidentDate
    ) {

        if (incidentDate.isAfter(LocalDateTime.now())) {

            throw new IllegalArgumentException(
                    "Incident date cannot be in the future"
            );
        }
    }


    // =========================================================
    // FIND CLAIM
    // =========================================================
    private Claim findClaimById(Long id) {

        return claimRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Claim not found with ID: " + id
                        )
                );
    }


    // =========================================================
    // VALIDATE ID
    // =========================================================
    private void validateId(Long id) {

        if (id == null || id <= 0) {

            throw new IllegalArgumentException(
                    "ID must be a valid positive number"
            );
        }
    }


    // =========================================================
    // GENERATE CLAIM NUMBER
    //
    // Example:
    // CLM-2026-00001
    // =========================================================
    private String generateClaimNumber() {

        int year = Year.now().getValue();

        long count = claimRepository.count() + 1;

        String claimNumber;

        do {

            claimNumber = String.format(
                    "CLM-%d-%05d",
                    year,
                    count
            );

            count++;

        } while (
                claimRepository
                        .existsByClaimNumber(claimNumber)
        );

        return claimNumber;
    }


    // =========================================================
    // CLEAN STRING
    // =========================================================
    private String cleanString(String value) {

        if (value == null) {
            return null;
        }

        return value.trim();
    }


    private boolean isBlank(String value) {

        return value == null || value.isBlank();
    }


    // =========================================================
    // ENTITY -> DTO
    // =========================================================
    private ClaimDTO mapToDTO(Claim claim) {

        return ClaimDTO.builder()
                .id(claim.getId())
                .claimNumber(claim.getClaimNumber())
                .customerId(claim.getCustomerId())
                .createdByEtfNo(claim.getCreatedByEtfNo())
                .claimType(claim.getClaimType())
                .status(claim.getStatus())
                .incidentDate(claim.getIncidentDate())
                .reportedDate(claim.getReportedDate())
                .incidentLocation(claim.getIncidentLocation())
                .incidentDescription(claim.getIncidentDescription())
                .situationStatement(claim.getSituationStatement())
                .branchCode(claim.getBranchCode())
                .createdAt(claim.getCreatedAt())
                .updatedAt(claim.getUpdatedAt())
                .build();
    }
}
