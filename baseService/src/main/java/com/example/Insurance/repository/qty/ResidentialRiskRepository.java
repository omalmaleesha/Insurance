package com.example.Insurance.repository.qty;

import com.example.Insurance.entities.qty.ResidentialRisk;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ResidentialRiskRepository
        extends JpaRepository<ResidentialRisk, Long> {

    List<ResidentialRisk> findByQuotationId(Long quotationId);

    Optional<ResidentialRisk> findFirstByQuotationId(Long quotationId);

}
