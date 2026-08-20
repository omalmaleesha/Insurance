package com.example.Insurance.services;


public interface PdfService {

    byte[] generateProposalPdf(Long proposalId);

}