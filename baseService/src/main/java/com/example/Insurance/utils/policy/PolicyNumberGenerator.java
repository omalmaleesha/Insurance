package com.example.Insurance.utils.policy;

import com.example.Insurance.repository.policy.PolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Year;

@Component
@RequiredArgsConstructor
public class PolicyNumberGenerator {

    private final PolicyRepository policyRepository;

    public String generate() {
        String prefix = "POL-" + Year.now().getValue() + "-";

        int next = policyRepository
                .findTopByPolicyNumberStartingWithOrderByIdDesc(prefix)
                .map(policy -> parseSequence(policy.getPolicyNumber(), prefix))
                .orElse(0) + 1;

        return prefix + String.format("%06d", next);
    }

    private int parseSequence(String policyNumber, String prefix) {
        try {
            return Integer.parseInt(policyNumber.substring(prefix.length()));
        } catch (Exception e) {
            return 0;
        }
    }
}
