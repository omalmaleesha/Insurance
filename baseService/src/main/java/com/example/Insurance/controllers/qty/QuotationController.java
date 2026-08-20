package com.example.Insurance.controllers.qty;


import com.example.Insurance.dto.qty.CreateQuotationRequest;
import com.example.Insurance.dto.qty.QuotationResponse;
import com.example.Insurance.services.QuotationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quotations")
@RequiredArgsConstructor
@CrossOrigin
public class QuotationController {

    private final QuotationService quotationService;

    /**
     * Create a new quotation
     */
    @PreAuthorize("hasAuthority('QUOTATION_CREATE')")
    @PostMapping
    public ResponseEntity<QuotationResponse> createQuotation(
            @Validated @RequestBody CreateQuotationRequest request,
            Authentication authentication) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(quotationService.createQuotation(request, authentication));
    }

    /**
     * Get quotation by id
     */
    @GetMapping("/{id}")
    public ResponseEntity<QuotationResponse> getQuotation(
            @PathVariable Long id,
            Authentication authentication) {

        return ResponseEntity.ok(
                quotationService.getQuotation(id, authentication));
    }

    /**
     * Get all quotations
     */
    @GetMapping
    public ResponseEntity<List<QuotationResponse>> getAllQuotations(
            Authentication authentication) {

        return ResponseEntity.ok(
                quotationService.getAllQuotations(authentication));
    }

    /**
     * Update quotation
     */
    @PutMapping("/{id}")
    public ResponseEntity<QuotationResponse> updateQuotation(
            @PathVariable Long id,
            @Validated @RequestBody CreateQuotationRequest request,
            Authentication authentication) {

        return ResponseEntity.ok(
                quotationService.updateQuotation(id, request, authentication));
    }

    /**
     * Delete quotation
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuotation(
            @PathVariable Long id,
            Authentication authentication) {

        quotationService.deleteQuotation(id, authentication);

        return ResponseEntity.noContent().build();
    }

    /**
     * Calculate premium
     */
    @PostMapping("/{id}/calculate")
    public ResponseEntity<QuotationResponse> calculatePremium(
            @PathVariable Long id,
            Authentication authentication) {

        return ResponseEntity.ok(
                quotationService.calculatePremium(id, authentication));
    }

    /**
     * Approve quotation
     */
    @PostMapping("/{id}/approve")
    public ResponseEntity<QuotationResponse> approveQuotation(
            @PathVariable Long id,
            Authentication authentication) {

        return ResponseEntity.ok(
                quotationService.approveQuotation(id, authentication));
    }

    /**
     * Reject quotation
     */
    @PostMapping("/{id}/reject")
    public ResponseEntity<QuotationResponse> rejectQuotation(
            @PathVariable Long id,
            @RequestParam String reason,
            Authentication authentication) {

        return ResponseEntity.ok(
                quotationService.rejectQuotation(id, reason, authentication));
    }

    /**
     * Mark quotation as issued
     */
    @PostMapping("/{id}/issue")
    public ResponseEntity<QuotationResponse> issueQuotation(
            @PathVariable Long id,
            Authentication authentication) {

        return ResponseEntity.ok(
                quotationService.issueQuotation(id, authentication));
    }

    /**
     * Duplicate quotation
     */
    @PostMapping("/{id}/duplicate")
    public ResponseEntity<QuotationResponse> duplicateQuotation(
            @PathVariable Long id,
            Authentication authentication) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(quotationService.duplicateQuotation(id, authentication));
    }

    /**
     * Download quotation PDF
     */
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadQuotationPdf(
            @PathVariable Long id,
            Authentication authentication) {

        return quotationService.downloadQuotationPdf(id, authentication);
    }

}