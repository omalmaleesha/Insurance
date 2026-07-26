package com.example.Insurance.services.impl;

import com.example.Insurance.entities.Proposal;
import com.example.Insurance.entities.ProposalSignature;
import com.example.Insurance.repository.ProposalRepository;
import com.example.Insurance.repository.ProposalSignatureRepository;
import com.example.Insurance.services.PdfService;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class PdfServiceImpl implements PdfService {

    private final ProposalRepository proposalRepository;
    private final ProposalSignatureRepository signatureRepository;

    @Override
    public byte[] generateProposalPdf(Long proposalId) {
        

        Proposal proposal = proposalRepository.findById(proposalId)
                .orElseThrow(() ->
                        new RuntimeException("Proposal not found"));

        ProposalSignature signature = signatureRepository
                .findByProposalId(proposalId)
                .orElse(null);

        try {

            ByteArrayOutputStream out = new ByteArrayOutputStream();

            Document document = new Document(PageSize.A4);

            PdfWriter.getInstance(document, out);

            document.open();

            Font titleFont =
                    new Font(Font.HELVETICA,18,Font.BOLD);

            Font heading =
                    new Font(Font.HELVETICA,13,Font.BOLD);

            Font normal =
                    new Font(Font.HELVETICA,11);

            Paragraph title =
                    new Paragraph("INSURANCE PROPOSAL",titleFont);

            title.setAlignment(Element.ALIGN_CENTER);

            document.add(title);

            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(2);

            table.setWidthPercentage(100);

            table.addCell("Proposal Number");
            table.addCell(proposal.getProposalNumber());

            table.addCell("Customer Name");
            table.addCell(proposal.getCustomerName());

            table.addCell("Email");
            table.addCell(proposal.getCustomerEmail());

            table.addCell("Phone");
            table.addCell(proposal.getCustomerPhone());

            table.addCell("Product");
            table.addCell(proposal.getProductName());

            table.addCell("Status");
            table.addCell(proposal.getStatus().name());

            document.add(table);

            document.add(new Paragraph(" "));
            document.add(new Paragraph("Customer Declaration",heading));
            document.add(new Paragraph(
                    "I hereby declare that the information provided is true and correct.",
                    normal));

            document.add(new Paragraph(" "));

            if(signature != null &&
                    signature.getSignatureBase64()!=null){

                byte[] imageBytes = Base64.getDecoder()
                        .decode(signature.getSignatureBase64()
                                .replace("data:image/png;base64,",""));

                Image image = Image.getInstance(imageBytes);

                image.scaleToFit(180,80);

                document.add(new Paragraph("Customer Signature:",heading));
                document.add(image);

                document.add(new Paragraph(
                        "Signed By : "+signature.getSignedBy(),
                        normal));

                document.add(new Paragraph(
                        "Signed At : "+signature.getSignedAt(),
                        normal));
            }

            document.close();

            return out.toByteArray();

        } catch (Exception e){

            throw new RuntimeException("Unable to generate PDF",e);

        }

    }

}