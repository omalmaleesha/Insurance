package com.example.Insurance.service.gReport.impl;

import com.example.Insurance.entities.Claim;
import com.example.Insurance.entities.customer.Customer;
import com.example.Insurance.entities.policy.Policy;

public record GReportDatabaseContext(
        Claim claim,
        Customer customer,
        Policy policy

) {
}