package com.example.Insurance.service.gReport.impl;

import com.example.Insurance.entities.Claim;
import com.example.Insurance.entities.ClaimDocument;
import com.example.Insurance.entities.customer.Customer;
import com.example.Insurance.entities.policy.Policy;
import java.util.List;

public record GReportProcessingContext(
        Claim claim,
        ClaimDocument claimForm,
        ClaimDocument customerStatement,
        Customer customer,
        List<Policy> policies
) {}