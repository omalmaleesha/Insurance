package com.example.Insurance.controllers;

import com.example.Insurance.dto.ClaimDocumentDTO;
import com.example.Insurance.service.ClaimDocumentService;
import com.example.Insurance.utils.types.ClaimDocumentType;
import com.example.Insurance.utils.types.DocumentSource;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/claims")
@RequiredArgsConstructor
public class ClaimDocumentController {

    private final ClaimDocumentService claimDocumentService;


    // =========================================================
    // UPLOAD DOCUMENT
    //
    // POST /api/claims/{claimId}/documents
    // =========================================================
    @PostMapping(
            value = "/{claimId}/documents",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ClaimDocumentDTO> uploadDocument(

            @PathVariable Long claimId,

            @RequestParam("file")
            MultipartFile file,

            @RequestParam("documentType")
            ClaimDocumentType documentType,

            @RequestParam("documentSource")
            DocumentSource documentSource,

            @RequestParam("uploadedByEtfNo")
            String uploadedByEtfNo
    ) {

        ClaimDocumentDTO document =
                claimDocumentService.uploadDocument(
                        claimId,
                        file,
                        documentType,
                        documentSource,
                        uploadedByEtfNo
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(document);
    }


    // =========================================================
    // GET ALL DOCUMENTS FOR A CLAIM
    //
    // GET /api/claims/{claimId}/documents
    // =========================================================
    @GetMapping("/{claimId}/documents")
    public ResponseEntity<List<ClaimDocumentDTO>> getDocumentsByClaimId(
            @PathVariable Long claimId
    ) {

        return ResponseEntity.ok(
                claimDocumentService
                        .getDocumentsByClaimId(claimId)
        );
    }


    // =========================================================
    // GET DOCUMENT METADATA BY DOCUMENT ID
    //
    // GET /api/claims/documents/{documentId}
    // =========================================================
    @GetMapping("/documents/{documentId}")
    public ResponseEntity<ClaimDocumentDTO> getDocumentById(
            @PathVariable Long documentId
    ) {

        return ResponseEntity.ok(
                claimDocumentService
                        .getDocumentById(documentId)
        );
    }


    // =========================================================
    // VERIFY DOCUMENT
    //
    // PATCH /api/claims/documents/{documentId}/verify
    // =========================================================
    @PatchMapping("/documents/{documentId}/verify")
    public ResponseEntity<ClaimDocumentDTO> verifyDocument(
            @PathVariable Long documentId
    ) {

        return ResponseEntity.ok(
                claimDocumentService
                        .verifyDocument(documentId)
        );
    }


    // =========================================================
    // REJECT DOCUMENT
    //
    // PATCH /api/claims/documents/{documentId}/reject
    // =========================================================
    @PatchMapping("/documents/{documentId}/reject")
    public ResponseEntity<ClaimDocumentDTO> rejectDocument(

            @PathVariable Long documentId,

            @RequestParam("reason")
            String reason
    ) {

        return ResponseEntity.ok(
                claimDocumentService
                        .rejectDocument(
                                documentId,
                                reason
                        )
        );
    }


    // =========================================================
    // DELETE DOCUMENT
    //
    // DELETE /api/claims/documents/{documentId}
    // =========================================================
    @DeleteMapping("/documents/{documentId}")
    public ResponseEntity<Void> deleteDocument(
            @PathVariable Long documentId
    ) {

        claimDocumentService
                .deleteDocument(documentId);

        return ResponseEntity.noContent().build();
    }
}
