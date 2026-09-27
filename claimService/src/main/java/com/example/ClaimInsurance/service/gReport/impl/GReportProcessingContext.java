package com.example.ClaimInsurance.service.gReport.impl;

import com.example.ClaimInsurance.entities.Claim;
import com.example.ClaimInsurance.entities.ClaimDocument;
import com.example.ClaimInsurance.entities.customer.Customer;
import com.example.ClaimInsurance.entities.policy.Policy;

import java.util.List;

public record GReportProcessingContext(
        Claim claim,
        ClaimDocument claimForm,
        ClaimDocument customerStatement,
        Customer customer,
        List<Policy> policies
) {}