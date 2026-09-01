package com.example.Insurance.repository.policy;

import com.example.Insurance.entities.policy.Policy;
import com.example.Insurance.utils.types.PolicyStatus;
import com.example.Insurance.utils.types.PolicyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PolicyRepository extends JpaRepository<Policy, Long> {

    Optional<Policy> findByPolicyNumber(String policyNumber);

    boolean existsByPolicyNumber(String policyNumber);

    List<Policy> findByStatus(PolicyStatus status);

    List<Policy> findByPolicyType(PolicyType policyType);

    List<Policy> findByCustomer_Id(Long customerId);

    List<Policy> findByExpiryDateBefore(LocalDate date);

    Optional<Policy> findTopByPolicyNumberStartingWithOrderByIdDesc(String prefix);

    @Query("""
            SELECT p FROM Policy p
            WHERE LOWER(p.policyNumber) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(COALESCE(p.productName, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(COALESCE(p.insuredName, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(COALESCE(p.riskLocation, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(COALESCE(p.insuredNic, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
            """)
    List<Policy> search(@Param("keyword") String keyword);
}
