package com.example.Insurance.services;

import com.example.Insurance.dto.customer.*;
import com.example.Insurance.utils.types.CustomerType;
import com.example.Insurance.utils.types.Status;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface CustomerService {

    PersonalCustomerResponseDTO createPersonalCustomer(
            PersonalCustomerCreateRequestDTO request,
            Authentication authentication);

    CorporateCustomerResponseDTO createCorporateCustomer(
            CorporateCustomerCreateRequestDTO request,
            Authentication authentication);

    PersonalCustomerResponseDTO updatePersonalCustomer(
            Long id,
            PersonalCustomerUpdateRequestDTO request,
            Authentication authentication);

    CorporateCustomerResponseDTO updateCorporateCustomer(
            Long id,
            CorporateCustomerUpdateRequestDTO request,
            Authentication authentication);

    Object getCustomerById(
            Long id,
            Authentication authentication);

    List<?> getAllCustomers(
            Authentication authentication);

    void deleteCustomer(
            Long id,
            Authentication authentication);

    List<?> getCustomersByType(
            CustomerType type,
            Authentication authentication);

    List<?> getCustomersByStatus(
            Status status,
            Authentication authentication);

    List<?> searchCustomers(
            String keyword,
            Authentication authentication);

    Object getCustomerByCode(
            String customerCode,
            Authentication authentication);

    Object getCustomerByEmail(
            String email,
            Authentication authentication);

}
