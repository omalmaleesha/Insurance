package com.example.Insurance.controllers.policy;

import com.example.Insurance.dto.PageResponse;
import com.example.Insurance.dto.policy.PolicyCreateRequestDTO;
import com.example.Insurance.dto.policy.PolicyRenewRequestDTO;
import com.example.Insurance.dto.policy.PolicyResponseDTO;
import com.example.Insurance.dto.policy.PolicyUpdateRequestDTO;
import com.example.Insurance.services.PolicyService;
import com.example.Insurance.utils.types.PolicyStatus;
import com.example.Insurance.utils.types.PolicyType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/policies")
@RequiredArgsConstructor
@CrossOrigin
public class PolicyController {

    private final PolicyService policyService;

    @PreAuthorize("hasAuthority('POLICY_CREATE')")
    @PostMapping
    public ResponseEntity<PolicyResponseDTO> createPolicy(
            @RequestBody PolicyCreateRequestDTO request,
            Authentication authentication) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(policyService.createPolicy(request, authentication));
    }

    @PreAuthorize("hasAuthority('POLICY_UPDATE')")
    @PutMapping("/{id}")
    public ResponseEntity<PolicyResponseDTO> updatePolicy(
            @PathVariable Long id,
            @RequestBody PolicyUpdateRequestDTO request,
            Authentication authentication) {

        return ResponseEntity.ok(policyService.updatePolicy(id, request, authentication));
    }

    @PreAuthorize("hasAuthority('POLICY_READ')")
    @GetMapping("/{id}")
    public ResponseEntity<PolicyResponseDTO> getPolicyById(
            @PathVariable Long id,
            Authentication authentication) {

        return ResponseEntity.ok(policyService.getPolicyById(id, authentication));
    }

    @PreAuthorize("hasAuthority('POLICY_READ')")
    @GetMapping("/number/{policyNumber}")
    public ResponseEntity<PolicyResponseDTO> getPolicyByNumber(
            @PathVariable String policyNumber,
            Authentication authentication) {

        return ResponseEntity.ok(policyService.getPolicyByNumber(policyNumber, authentication));
    }

    @PreAuthorize("hasAuthority('POLICY_READ')")
    @GetMapping
    public ResponseEntity<PageResponse<PolicyResponseDTO>> getAllPolicies(
            @PageableDefault(page = 0, size = 50, sort = "id", direction = Sort.Direction.DESC)
            Pageable pageable,
            Authentication authentication) {

        return ResponseEntity.ok(policyService.getAllPolicies(authentication, pageable));
    }

    @PreAuthorize("hasAuthority('POLICY_READ')")
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<PolicyResponseDTO>> getPoliciesByCustomer(
            @PathVariable Long customerId,
            Authentication authentication) {

        return ResponseEntity.ok(policyService.getPoliciesByCustomer(customerId, authentication));
    }

    @PreAuthorize("hasAuthority('POLICY_READ')")
    @GetMapping("/status/{status}")
    public ResponseEntity<List<PolicyResponseDTO>> getPoliciesByStatus(
            @PathVariable PolicyStatus status,
            Authentication authentication) {

        return ResponseEntity.ok(policyService.getPoliciesByStatus(status, authentication));
    }

    @PreAuthorize("hasAuthority('POLICY_READ')")
    @GetMapping("/type/{type}")
    public ResponseEntity<List<PolicyResponseDTO>> getPoliciesByType(
            @PathVariable PolicyType type,
            Authentication authentication) {

        return ResponseEntity.ok(policyService.getPoliciesByType(type, authentication));
    }

    @PreAuthorize("hasAuthority('POLICY_READ')")
    @GetMapping("/search")
    public ResponseEntity<List<PolicyResponseDTO>> searchPolicies(
            @RequestParam String keyword,
            Authentication authentication) {

        return ResponseEntity.ok(policyService.searchPolicies(keyword, authentication));
    }

    @PreAuthorize("hasAuthority('POLICY_ISSUE')")
    @PostMapping("/{id}/issue")
    public ResponseEntity<PolicyResponseDTO> issuePolicy(
            @PathVariable Long id,
            Authentication authentication) {

        return ResponseEntity.ok(policyService.issuePolicy(id, authentication));
    }

    @PreAuthorize("hasAuthority('POLICY_UPDATE')")
    @PostMapping("/{id}/suspend")
    public ResponseEntity<PolicyResponseDTO> suspendPolicy(
            @PathVariable Long id,
            Authentication authentication) {

        return ResponseEntity.ok(policyService.suspendPolicy(id, authentication));
    }

    @PreAuthorize("hasAuthority('POLICY_UPDATE')")
    @PostMapping("/{id}/activate")
    public ResponseEntity<PolicyResponseDTO> activatePolicy(
            @PathVariable Long id,
            Authentication authentication) {

        return ResponseEntity.ok(policyService.activatePolicy(id, authentication));
    }

    @PreAuthorize("hasAuthority('POLICY_CANCEL')")
    @PostMapping("/{id}/cancel")
    public ResponseEntity<PolicyResponseDTO> cancelPolicy(
            @PathVariable Long id,
            @RequestParam String reason,
            Authentication authentication) {

        return ResponseEntity.ok(policyService.cancelPolicy(id, reason, authentication));
    }

    @PreAuthorize("hasAuthority('POLICY_UPDATE')")
    @PostMapping("/{id}/expire")
    public ResponseEntity<PolicyResponseDTO> markExpired(
            @PathVariable Long id,
            Authentication authentication) {

        return ResponseEntity.ok(policyService.markExpired(id, authentication));
    }

    @PreAuthorize("hasAuthority('POLICY_UPDATE')")
    @PostMapping("/{id}/payments")
    public ResponseEntity<PolicyResponseDTO> recordPremiumPayment(
            @PathVariable Long id,
            @RequestParam BigDecimal amount,
            Authentication authentication) {

        return ResponseEntity.ok(policyService.recordPremiumPayment(id, amount, authentication));
    }

    @PreAuthorize("hasAuthority('POLICY_RENEW')")
    @PostMapping("/{id}/renew")
    public ResponseEntity<PolicyResponseDTO> renewPolicy(
            @PathVariable Long id,
            @RequestBody(required = false) PolicyRenewRequestDTO request,
            Authentication authentication) {

        if (request == null) {
            request = new PolicyRenewRequestDTO();
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(policyService.renewPolicy(id, request, authentication));
    }

    @PreAuthorize("hasAuthority('POLICY_DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePolicy(
            @PathVariable Long id,
            Authentication authentication) {

        policyService.deletePolicy(id, authentication);
        return ResponseEntity.noContent().build();
    }
}
