package com.example.Insurance.repository.qty;

import com.example.Insurance.entities.qty.IndustrialRisk;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IndustrialRiskRepository
        extends JpaRepository<IndustrialRisk, Long> {

    List<IndustrialRisk> findByQuotationId(Long quotationId);

    Optional<IndustrialRisk> findFirstByQuotationId(Long quotationId);

}