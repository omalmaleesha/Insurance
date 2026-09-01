package com.example.Insurance.dto.policy;

import com.example.Insurance.utils.types.CoverType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PolicyCoverageDTO {

    private Long id;

    private String coverCode;

    private String coverName;

    private CoverType coverType;

    private BigDecimal sumInsured;

    private BigDecimal rate;

    private BigDecimal premium;

    private String description;

    private Boolean optional;
}
