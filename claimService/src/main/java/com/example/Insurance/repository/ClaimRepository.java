package com.example.Insurance.repository;

import com.example.Insurance.entities.Claim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, Long> {
    Optional<Claim> findByClaimNumber(String claimNumber);
    List<Claim> findByCustomerId(Long customerId);
    boolean existsByClaimNumber(String claimNumber);
}