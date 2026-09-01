package com.example.Insurance.repository;


import com.example.Insurance.entities.policy.Policy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PolicyRepository extends JpaRepository<Policy, Long> {

    List<Policy> findByCustomerId(Long customerId);

    Optional<Policy> findByPolicyNumber(String policyNumber);

    Optional<Policy> findByPolicyNumberAndCustomerId(
            String policyNumber,
            Long customerId
    );
}