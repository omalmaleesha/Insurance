package com.example.Insurance.controllers;

import com.example.Insurance.dto.customer.*;
import com.example.Insurance.services.CustomerService;
import com.example.Insurance.utils.types.CustomerType;
import com.example.Insurance.utils.types.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@CrossOrigin
public class CustomerController {

    private final CustomerService customerService;

    // ===========================
    // PERSONAL CUSTOMERS
    // ===========================

    @PostMapping("/personal")
    public ResponseEntity<PersonalCustomerResponseDTO> createPersonalCustomer(
            @RequestBody PersonalCustomerCreateRequestDTO request,
            Authentication authentication) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(customerService.createPersonalCustomer(request, authentication));
    }

    @PutMapping("/personal/{id}")
    public ResponseEntity<PersonalCustomerResponseDTO> updatePersonalCustomer(
            @PathVariable Long id,
            @RequestBody PersonalCustomerUpdateRequestDTO request,
            Authentication authentication) {

        return ResponseEntity.ok(
                customerService.updatePersonalCustomer(id, request, authentication));
    }

    // ===========================
    // CORPORATE CUSTOMERS
    // ===========================

    @PostMapping("/corporate")
    public ResponseEntity<CorporateCustomerResponseDTO> createCorporateCustomer(
            @RequestBody CorporateCustomerCreateRequestDTO request,
            Authentication authentication) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(customerService.createCorporateCustomer(request, authentication));
    }

    @PutMapping("/corporate/{id}")
    public ResponseEntity<CorporateCustomerResponseDTO> updateCorporateCustomer(
            @PathVariable Long id,
            @RequestBody CorporateCustomerUpdateRequestDTO request,
            Authentication authentication) {

        return ResponseEntity.ok(
                customerService.updateCorporateCustomer(id, request, authentication));
    }

    // ===========================
    // COMMON OPERATIONS
    // ===========================

    @GetMapping("/{id}")
    public ResponseEntity<?> getCustomerById(
            @PathVariable Long id,
            Authentication authentication) {

        return ResponseEntity.ok(
                customerService.getCustomerById(id, authentication));
    }

    @GetMapping
    public ResponseEntity<List<?>> getAllCustomers(
            Authentication authentication) {

        return ResponseEntity.ok(
                customerService.getAllCustomers(authentication));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(
            @PathVariable Long id,
            Authentication authentication) {

        customerService.deleteCustomer(id, authentication);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<?>> getCustomersByType(
            @PathVariable CustomerType type,
            Authentication authentication) {

        return ResponseEntity.ok(
                customerService.getCustomersByType(type, authentication));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<?>> getCustomersByStatus(
            @PathVariable Status status,
            Authentication authentication) {

        return ResponseEntity.ok(
                customerService.getCustomersByStatus(status, authentication));
    }

    @GetMapping("/search")
    public ResponseEntity<List<?>> searchCustomers(
            @RequestParam String keyword,
            Authentication authentication) {

        return ResponseEntity.ok(
                customerService.searchCustomers(keyword, authentication));
    }

    @GetMapping("/code/{customerCode}")
    public ResponseEntity<?> getCustomerByCode(
            @PathVariable String customerCode,
            Authentication authentication) {

        return ResponseEntity.ok(
                customerService.getCustomerByCode(customerCode, authentication));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<?> getCustomerByEmail(
            @PathVariable String email,
            Authentication authentication) {

        return ResponseEntity.ok(
                customerService.getCustomerByEmail(email, authentication));
    }
}