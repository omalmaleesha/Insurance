package com.example.ClaimInsurance.service.gReport.impl;

import com.example.ClaimInsurance.entities.Claim;
import com.example.ClaimInsurance.entities.customer.Customer;
import com.example.ClaimInsurance.entities.policy.Policy;

public record GReportDatabaseContext(
        Claim claim,
        Customer customer,
        Policy policy

) {
}