package com.example.Insurance.dto.delivery;

import com.example.Insurance.utils.DeliveryCondition;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryConfirmationRequest {

    private DeliveryCondition condition;

    private String remarks;

}
