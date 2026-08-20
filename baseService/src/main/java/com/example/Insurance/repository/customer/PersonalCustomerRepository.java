package com.example.Insurance.repository.customer;

import com.example.Insurance.entities.customer.PersonalCustomer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface PersonalCustomerRepository extends JpaRepository<PersonalCustomer, Long> {

    Optional<PersonalCustomer> findByNic(String nic);

}