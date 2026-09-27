package com.example.ClaimInsurance.service.gReport.impl;

import com.example.ClaimInsurance.entities.ClaimDocument;
import com.example.ClaimInsurance.service.gReport.GReportDocumentService;
import com.example.ClaimInsurance.service.storage.FileStorageService;
import com.example.ClaimInsurance.service.storage.StorageFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class GReportDocumentServiceImpl implements GReportDocumentService {

    private final FileStorageService fileStorageService;

    @Override
    public String extractText(ClaimDocument document) {
        log.info(
                "Starting PDF extraction. documentId={}, fileName={}",
                document.getId(),
                document.getFileName()
        );
        StorageFile storageFile = fileStorageService.download(document.getStorageFileId());

        try {
            byte[] pdfBytes = storageFile.inputStream().readAllBytes();
            try (var pdfDocument = Loader.loadPDF(pdfBytes)) {
                PDFTextStripper stripper = new PDFTextStripper();
                String text = stripper.getText(pdfDocument);
                if (text == null || text.isBlank()) {
                    throw new RuntimeException(
                            "No readable text found in document: " + document.getId()
                    );
                }
                log.info(
                        "PDF extraction completed. documentId={}, characters={}",
                        document.getId(),
                        text.length()
                );

                return text;
            }
        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to extract PDF text. documentId=" + document.getId(),
                    e
            );
        }
    }
}