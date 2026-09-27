package com.example.ClaimInsurance.repository;

import com.example.ClaimInsurance.entities.customer.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository
        extends JpaRepository<Customer, Long> {
}