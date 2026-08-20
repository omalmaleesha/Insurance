package com.example.Insurance.repository.customer;

import com.example.Insurance.entities.customer.CorporateCustomer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CorporateCustomerRepository extends JpaRepository<CorporateCustomer, Long> {

    Optional<CorporateCustomer> findByRegistrationNumber(String registrationNumber);

}
