package com.example.Insurance.dto.proposal;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProposalFormRequest {

    private String token;

    private String customerName;

    private String customerPhone;

    private String address;

    private LocalDate dateOfBirth;

    private String nic;

    private String occupation;

    private String signatureBase64;

}