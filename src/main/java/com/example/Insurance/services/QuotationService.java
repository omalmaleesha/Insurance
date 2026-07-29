package com.example.Insurance.services;

import com.example.Insurance.dto.qty.CreateQuotationRequest;
import com.example.Insurance.dto.qty.QuotationResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface QuotationService {

    QuotationResponse createQuotation(
            CreateQuotationRequest request,
            Authentication authentication);

    QuotationResponse getQuotation(
            Long id,
            Authentication authentication);

    List<QuotationResponse> getAllQuotations(
            Authentication authentication);

    QuotationResponse updateQuotation(
            Long id,
            CreateQuotationRequest request,
            Authentication authentication);

    void deleteQuotation(
            Long id,
            Authentication authentication);

    QuotationResponse calculatePremium(
            Long id,
            Authentication authentication);

    QuotationResponse approveQuotation(
            Long id,
            Authentication authentication);

    QuotationResponse rejectQuotation(
            Long id,
            String reason,
            Authentication authentication);

    QuotationResponse issueQuotation(
            Long id,
            Authentication authentication);

    QuotationResponse duplicateQuotation(
            Long id,
            Authentication authentication);

    ResponseEntity<byte[]> downloadQuotationPdf(
            Long id,
            Authentication authentication);

}