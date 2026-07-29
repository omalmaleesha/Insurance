package com.example.Insurance.repository.qty;

import com.example.Insurance.entities.qty.CommercialRisk;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommercialRiskRepository
        extends JpaRepository<CommercialRisk, Long> {

    List<CommercialRisk> findByQuotationId(Long quotationId);

    Optional<CommercialRisk> findFirstByQuotationId(Long quotationId);

}