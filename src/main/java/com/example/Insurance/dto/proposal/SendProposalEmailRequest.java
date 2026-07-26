package com.example.Insurance.dto.proposal;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SendProposalEmailRequest {

    private Long proposalId;

}