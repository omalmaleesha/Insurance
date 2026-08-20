package com.example.Insurance.dto.delivery;

import com.example.Insurance.utils.types.DeliveryCondition;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryConfirmationResponse {

    private Long id;

    private String receivedBy;

    private LocalDateTime receivedDate;

    private DeliveryCondition condition;

    private String remarks;

}
