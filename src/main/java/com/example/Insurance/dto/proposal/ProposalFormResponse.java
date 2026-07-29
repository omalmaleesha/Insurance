package com.example.Insurance.dto.proposal;

import com.example.Insurance.utils.types.ProposalStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProposalFormResponse {

    private String proposalNumber;

    private String customerName;

    private String customerEmail;

    private ProposalStatus status;

}