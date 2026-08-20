package com.example.Insurance.repository.qty;

import com.example.Insurance.entities.qty.Quotation;
import com.example.Insurance.utils.types.QuotationStatus;
import com.example.Insurance.utils.types.QuotationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuotationRepository extends JpaRepository<Quotation, Long> {

    Optional<Quotation> findByQuotationReference(String quotationReference);

    boolean existsByQuotationReference(String quotationReference);

    List<Quotation> findByStatus(QuotationStatus status);

    List<Quotation> findByQuotationType(QuotationType quotationType);

    List<Quotation> findByCustomerNameContainingIgnoreCase(String customerName);

}
