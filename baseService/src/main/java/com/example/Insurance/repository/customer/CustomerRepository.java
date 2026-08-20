package com.example.Insurance.repository.customer;

import com.example.Insurance.entities.customer.Customer;
import com.example.Insurance.entities.customer.PersonalCustomer;
import com.example.Insurance.utils.types.CustomerType;
import com.example.Insurance.utils.types.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByCustomerCode(String customerCode);

    Optional<Customer> findByEmail(String email);

    List<Customer> findByCustomerType(CustomerType customerType);

    List<Customer> findByStatus(Status status);

    List<PersonalCustomer> findByFirstNameContainingIgnoreCase(String name);

    List<PersonalCustomer> findByLastNameContainingIgnoreCase(String name);

    List<PersonalCustomer> findByNicContainingIgnoreCase(String nic);

}