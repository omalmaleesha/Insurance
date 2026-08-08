package com.example.Insurance.services.impl;

import com.example.Insurance.dto.PageResponse;
import com.example.Insurance.dto.customer.*;
import com.example.Insurance.entities.CorporateCustomer;
import com.example.Insurance.entities.Customer;
import com.example.Insurance.entities.PersonalCustomer;
import com.example.Insurance.repository.customer.CorporateCustomerRepository;
import com.example.Insurance.repository.customer.CustomerRepository;
import com.example.Insurance.repository.customer.PersonalCustomerRepository;
import com.example.Insurance.services.CustomerService;
import com.example.Insurance.utils.types.CustomerType;
import com.example.Insurance.utils.types.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final PersonalCustomerRepository personalCustomerRepository;
    private final CustomerRepository customerRepository;
    private final CorporateCustomerRepository corporateCustomerRepository;

    @Override
    public PersonalCustomerResponseDTO createPersonalCustomer(
            PersonalCustomerCreateRequestDTO request,
            Authentication authentication) {

        validateCustomerCode(request.getCustomerCode());
        validateEmail(request.getEmail());
        validateNic(request.getNic());

        PersonalCustomer customer = new PersonalCustomer();

        customer.setCustomerCode(request.getCustomerCode());
        customer.setCustomerType(CustomerType.PERSONAL);

        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setNic(request.getNic());
        customer.setDateOfBirth(request.getDateOfBirth());
        customer.setGender(request.getGender());
        customer.setOccupation(request.getOccupation());

        customer.setEmail(request.getEmail());
        customer.setPhoneNumber(request.getPhoneNumber());
        customer.setAddress(request.getAddress());
        customer.setCity(request.getCity());
        customer.setCountry(request.getCountry());

        customer.setStatus(Status.ACTIVE);
        customer.setCreatedAt(LocalDateTime.now());
        customer.setUpdatedAt(LocalDateTime.now());

        personalCustomerRepository.save(customer);

        return mapPersonal(customer);
    }


    @Override
    public CorporateCustomerResponseDTO createCorporateCustomer(
            CorporateCustomerCreateRequestDTO request,
            Authentication authentication) {

        validateCustomerCode(request.getCustomerCode());
        validateEmail(request.getEmail());
        validateRegistrationNumber(request.getRegistrationNumber());

        CorporateCustomer customer = new CorporateCustomer();

        customer.setCustomerCode(request.getCustomerCode());
        customer.setCustomerType(CustomerType.CORPORATE);

        customer.setCompanyName(request.getCompanyName());
        customer.setRegistrationNumber(request.getRegistrationNumber());
        customer.setIndustry(request.getIndustry());
        customer.setContactPerson(request.getContactPerson());
        customer.setContactDesignation(request.getContactDesignation());
        customer.setContactPhone(request.getContactPhone());
        customer.setWebsite(request.getWebsite());

        customer.setEmail(request.getEmail());
        customer.setPhoneNumber(request.getPhoneNumber());
        customer.setAddress(request.getAddress());
        customer.setCity(request.getCity());
        customer.setCountry(request.getCountry());

        customer.setStatus(Status.ACTIVE);
        customer.setCreatedAt(LocalDateTime.now());
        customer.setUpdatedAt(LocalDateTime.now());

        corporateCustomerRepository.save(customer);

        return mapCorporate(customer);
    }

    private void validateCustomerCode(String customerCode){

        if(customerRepository.findByCustomerCode(customerCode).isPresent()){
            throw new RuntimeException("Customer Code already exists.");
        }

    }

    private void validateEmail(String email){

        if(customerRepository.findByEmail(email).isPresent()){
            throw new RuntimeException("Email already exists.");
        }

    }

    private void validateNic(String nic){

        if(personalCustomerRepository.findByNic(nic).isPresent()){
            throw new RuntimeException("NIC already exists.");
        }

    }

    private void validateRegistrationNumber(String registration){

        if(corporateCustomerRepository.findByRegistrationNumber(registration).isPresent()){
            throw new RuntimeException("Registration Number already exists.");
        }

    }

    private PersonalCustomerResponseDTO mapPersonal(PersonalCustomer customer){

        PersonalCustomerResponseDTO dto = new PersonalCustomerResponseDTO();

        dto.setId(customer.getId());
        dto.setCustomerCode(customer.getCustomerCode());

        dto.setFirstName(customer.getFirstName());
        dto.setLastName(customer.getLastName());
        dto.setNic(customer.getNic());
        dto.setDateOfBirth(customer.getDateOfBirth());
        dto.setGender(customer.getGender());
        dto.setOccupation(customer.getOccupation());

        dto.setEmail(customer.getEmail());
        dto.setPhoneNumber(customer.getPhoneNumber());
        dto.setAddress(customer.getAddress());
        dto.setCity(customer.getCity());
        dto.setCountry(customer.getCountry());

        dto.setStatus(customer.getStatus());
        dto.setCreatedAt(customer.getCreatedAt());
        dto.setUpdatedAt(customer.getUpdatedAt());

        return dto;
    }

    private CorporateCustomerResponseDTO mapCorporate(CorporateCustomer customer){

        CorporateCustomerResponseDTO dto = new CorporateCustomerResponseDTO();

        dto.setId(customer.getId());
        dto.setCustomerCode(customer.getCustomerCode());

        dto.setCompanyName(customer.getCompanyName());
        dto.setRegistrationNumber(customer.getRegistrationNumber());
        dto.setIndustry(customer.getIndustry());
        dto.setContactPerson(customer.getContactPerson());
        dto.setContactDesignation(customer.getContactDesignation());
        dto.setContactPhone(customer.getContactPhone());
        dto.setWebsite(customer.getWebsite());

        dto.setEmail(customer.getEmail());
        dto.setPhoneNumber(customer.getPhoneNumber());
        dto.setAddress(customer.getAddress());
        dto.setCity(customer.getCity());
        dto.setCountry(customer.getCountry());

        dto.setStatus(customer.getStatus());
        dto.setCreatedAt(customer.getCreatedAt());
        dto.setUpdatedAt(customer.getUpdatedAt());

        return dto;
    }

    @Override
    public PersonalCustomerResponseDTO updatePersonalCustomer(
            Long id,
            PersonalCustomerUpdateRequestDTO request,
            Authentication authentication) {

        PersonalCustomer customer = personalCustomerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Personal customer not found."));

        // Check duplicate email
        Optional<Customer> emailCustomer = customerRepository.findByEmail(request.getEmail());
        if (emailCustomer.isPresent() && !emailCustomer.get().getId().equals(id)) {
            throw new RuntimeException("Email already exists.");
        }

        // Check duplicate NIC
        Optional<PersonalCustomer> nicCustomer = personalCustomerRepository.findByNic(request.getNic());
        if (nicCustomer.isPresent() && !nicCustomer.get().getId().equals(id)) {
            throw new RuntimeException("NIC already exists.");
        }

        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setNic(request.getNic());
        customer.setDateOfBirth(request.getDateOfBirth());
        customer.setGender(request.getGender());
        customer.setOccupation(request.getOccupation());

        customer.setEmail(request.getEmail());
        customer.setPhoneNumber(request.getPhoneNumber());
        customer.setAddress(request.getAddress());
        customer.setCity(request.getCity());
        customer.setCountry(request.getCountry());

        customer.setUpdatedAt(LocalDateTime.now());

        personalCustomerRepository.save(customer);

        return mapPersonal(customer);
    }

    @Override
    public CorporateCustomerResponseDTO updateCorporateCustomer(
            Long id,
            CorporateCustomerUpdateRequestDTO request,
            Authentication authentication) {

        CorporateCustomer customer = corporateCustomerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Corporate customer not found."));

        // Check duplicate email
        Optional<Customer> emailCustomer = customerRepository.findByEmail(request.getEmail());
        if (emailCustomer.isPresent() && !emailCustomer.get().getId().equals(id)) {
            throw new RuntimeException("Email already exists.");
        }

        // Check duplicate registration number
        Optional<CorporateCustomer> regCustomer =
                corporateCustomerRepository.findByRegistrationNumber(request.getRegistrationNumber());

        if (regCustomer.isPresent() && !regCustomer.get().getId().equals(id)) {
            throw new RuntimeException("Registration number already exists.");
        }

        customer.setCompanyName(request.getCompanyName());
        customer.setRegistrationNumber(request.getRegistrationNumber());
        customer.setIndustry(request.getIndustry());
        customer.setContactPerson(request.getContactPerson());
        customer.setContactDesignation(request.getContactDesignation());
        customer.setContactPhone(request.getContactPhone());
        customer.setWebsite(request.getWebsite());

        customer.setEmail(request.getEmail());
        customer.setPhoneNumber(request.getPhoneNumber());
        customer.setAddress(request.getAddress());
        customer.setCity(request.getCity());
        customer.setCountry(request.getCountry());

        customer.setUpdatedAt(LocalDateTime.now());

        corporateCustomerRepository.save(customer);

        return mapCorporate(customer);
    }

//    @Override
//    public void deleteCustomer(
//            Long id,
//            Authentication authentication) {
//
//        Customer customer = customerRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Customer not found."));
//
//        customerRepository.delete(customer);
//    }


    //in corperation without delete pernanaty a custemer just inactive
    @Override
    public void deleteCustomer(
            Long id,
            Authentication authentication) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found."));

        customer.setStatus(Status.INACTIVE);
        customer.setUpdatedAt(LocalDateTime.now());

        customerRepository.save(customer);
    }

    @Override
    public Object getCustomerById(
            Long id,
            Authentication authentication) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found."));

        if (customer instanceof PersonalCustomer personalCustomer) {
            return mapPersonal(personalCustomer);
        }

        if (customer instanceof CorporateCustomer corporateCustomer) {
            return mapCorporate(corporateCustomer);
        }

        throw new RuntimeException("Invalid customer type.");
    }

    @Override
    public PageResponse<?> getAllCustomers(
            Authentication authentication,
            Pageable pageable) {

        Page<Customer> customerPage =
                customerRepository.findAll(pageable);

        List<?> customers = customerPage.getContent()
                .stream()
                .map(customer -> {

                    if (customer instanceof PersonalCustomer personalCustomer) {
                        return mapPersonal(personalCustomer);
                    }

                    if (customer instanceof CorporateCustomer corporateCustomer) {
                        return mapCorporate(corporateCustomer);
                    }

                    throw new RuntimeException("Invalid customer type.");
                })
                .toList();

        return new PageResponse<>(
                customers,
                customerPage.getNumber(),
                customerPage.getSize(),
                customerPage.getTotalElements(),
                customerPage.getTotalPages(),
                customerPage.isFirst(),
                customerPage.isLast()
        );
    }

    @Override
    public List<?> getCustomersByType(
            CustomerType type,
            Authentication authentication) {

        return customerRepository.findByCustomerType(type)
                .stream()
                .map(customer -> {

                    if (customer instanceof PersonalCustomer personalCustomer) {
                        return mapPersonal(personalCustomer);
                    }

                    return mapCorporate((CorporateCustomer) customer);

                })
                .toList();
    }

    @Override
    public List<?> getCustomersByStatus(
            Status status,
            Authentication authentication) {

        return customerRepository.findByStatus(status)
                .stream()
                .map(customer -> {

                    if (customer instanceof PersonalCustomer personalCustomer) {
                        return mapPersonal(personalCustomer);
                    }

                    return mapCorporate((CorporateCustomer) customer);

                })
                .toList();
    }

    @Override
    public List<?> searchCustomers(
            String keyword,
            Authentication authentication) {

        return customerRepository.findAll()
                .stream()
                .filter(customer -> {

                    if (customer.getCustomerCode() != null &&
                            customer.getCustomerCode().toLowerCase().contains(keyword.toLowerCase()))
                        return true;

                    if (customer.getEmail() != null &&
                            customer.getEmail().toLowerCase().contains(keyword.toLowerCase()))
                        return true;

                    if (customer instanceof PersonalCustomer personal) {

                        String fullName = personal.getFirstName() + " " + personal.getLastName();

                        if (fullName.toLowerCase().contains(keyword.toLowerCase()))
                            return true;

                        if (personal.getNic() != null &&
                                personal.getNic().toLowerCase().contains(keyword.toLowerCase()))
                            return true;
                    }

                    if (customer instanceof CorporateCustomer corporate) {

                        if (corporate.getCompanyName() != null &&
                                corporate.getCompanyName().toLowerCase().contains(keyword.toLowerCase()))
                            return true;

                        if (corporate.getRegistrationNumber() != null &&
                                corporate.getRegistrationNumber().toLowerCase().contains(keyword.toLowerCase()))
                            return true;
                    }

                    return false;

                })
                .map(customer -> {

                    if (customer instanceof PersonalCustomer personal) {
                        return mapPersonal(personal);
                    }

                    return mapCorporate((CorporateCustomer) customer);

                })
                .toList();
    }

    @Override
    public Object getCustomerByCode(
            String customerCode,
            Authentication authentication) {

        Customer customer = customerRepository.findByCustomerCode(customerCode)
                .orElseThrow(() -> new RuntimeException("Customer not found."));

        if (customer instanceof PersonalCustomer personalCustomer) {
            return mapPersonal(personalCustomer);
        }

        return mapCorporate((CorporateCustomer) customer);
    }

    @Override
    public Object getCustomerByEmail(
            String email,
            Authentication authentication) {

        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Customer not found."));

        if (customer instanceof PersonalCustomer personalCustomer) {
            return mapPersonal(personalCustomer);
        }

        return mapCorporate((CorporateCustomer) customer);
    }

}
