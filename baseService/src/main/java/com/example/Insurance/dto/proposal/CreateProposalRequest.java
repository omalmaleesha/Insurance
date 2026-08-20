package com.example.Insurance.dto.proposal;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProposalRequest {

    private String customerName;

    private String customerEmail;

    private String customerPhone;

    private String productName;

}