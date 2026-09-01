package com.example.Insurance.services;

import com.example.Insurance.dto.PageResponse;
import com.example.Insurance.dto.policy.PolicyCreateRequestDTO;
import com.example.Insurance.dto.policy.PolicyRenewRequestDTO;
import com.example.Insurance.dto.policy.PolicyResponseDTO;
import com.example.Insurance.dto.policy.PolicyUpdateRequestDTO;
import com.example.Insurance.utils.types.PolicyStatus;
import com.example.Insurance.utils.types.PolicyType;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.util.List;

public interface PolicyService {

    PolicyResponseDTO createPolicy(PolicyCreateRequestDTO request, Authentication authentication);

    PolicyResponseDTO updatePolicy(Long id, PolicyUpdateRequestDTO request, Authentication authentication);

    PolicyResponseDTO getPolicyById(Long id, Authentication authentication);

    PolicyResponseDTO getPolicyByNumber(String policyNumber, Authentication authentication);

    PageResponse<PolicyResponseDTO> getAllPolicies(Authentication authentication, Pageable pageable);

    List<PolicyResponseDTO> getPoliciesByCustomer(Long customerId, Authentication authentication);

    List<PolicyResponseDTO> getPoliciesByStatus(PolicyStatus status, Authentication authentication);

    List<PolicyResponseDTO> getPoliciesByType(PolicyType type, Authentication authentication);

    List<PolicyResponseDTO> searchPolicies(String keyword, Authentication authentication);

    PolicyResponseDTO issuePolicy(Long id, Authentication authentication);

    PolicyResponseDTO suspendPolicy(Long id, Authentication authentication);

    PolicyResponseDTO activatePolicy(Long id, Authentication authentication);

    PolicyResponseDTO cancelPolicy(Long id, String reason, Authentication authentication);

    PolicyResponseDTO markExpired(Long id, Authentication authentication);

    PolicyResponseDTO recordPremiumPayment(Long id, BigDecimal amount, Authentication authentication);

    PolicyResponseDTO renewPolicy(Long id, PolicyRenewRequestDTO request, Authentication authentication);

    void deletePolicy(Long id, Authentication authentication);
}
