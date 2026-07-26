package com.example.Insurance.dto.proposal;

import com.example.Insurance.utils.ProposalStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProposalResponse {

    private Long id;

    private String proposalNumber;

    private String customerName;

    private String customerEmail;

    private String customerPhone;

    private String productName;

    private ProposalStatus status;

}