package com.example.Insurance.dto.proposal;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailResponse {

    private boolean success;

    private String message;

}