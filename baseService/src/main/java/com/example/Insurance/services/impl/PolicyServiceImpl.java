package com.example.Insurance.services.impl;

import com.example.Insurance.dto.PageResponse;
import com.example.Insurance.dto.policy.PolicyCoverageDTO;
import com.example.Insurance.dto.policy.PolicyCreateRequestDTO;
import com.example.Insurance.dto.policy.PolicyRenewRequestDTO;
import com.example.Insurance.dto.policy.PolicyResponseDTO;
import com.example.Insurance.dto.policy.PolicyUpdateRequestDTO;
import com.example.Insurance.entities.User;
import com.example.Insurance.entities.customer.Customer;
import com.example.Insurance.entities.fileTrasfer.Branch;
import com.example.Insurance.entities.policy.Policy;
import com.example.Insurance.entities.policy.PolicyCoverage;
import com.example.Insurance.entities.propsal.Proposal;
import com.example.Insurance.entities.qty.Quotation;
import com.example.Insurance.repository.UserRepository;
import com.example.Insurance.repository.customer.CustomerRepository;
import com.example.Insurance.repository.fileTrasfer.BranchRepository;
import com.example.Insurance.repository.policy.PolicyRepository;
import com.example.Insurance.repository.proposal.ProposalRepository;
import com.example.Insurance.repository.qty.QuotationRepository;
import com.example.Insurance.services.PolicyService;
import com.example.Insurance.utils.policy.PolicyNumberGenerator;
import com.example.Insurance.utils.types.CurrencyType;
import com.example.Insurance.utils.types.PaymentFrequency;
import com.example.Insurance.utils.types.PolicyStatus;
import com.example.Insurance.utils.types.PolicyType;
import com.example.Insurance.utils.types.PremiumPaymentStatus;
import com.example.Insurance.utils.types.QuotationType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PolicyServiceImpl implements PolicyService {

    private final PolicyRepository policyRepository;
    private final CustomerRepository customerRepository;
    private final QuotationRepository quotationRepository;
    private final ProposalRepository proposalRepository;
    private final BranchRepository branchRepository;
    private final UserRepository userRepository;
    private final PolicyNumberGenerator policyNumberGenerator;

    @Override
    @Transactional
    public PolicyResponseDTO createPolicy(
            PolicyCreateRequestDTO request,
            Authentication authentication) {

        User currentUser = currentUser(authentication);

        if (request.getCustomerId() == null) {
            throw new RuntimeException("Customer is required.");
        }

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found."));

        Policy policy = new Policy();
        policy.setPolicyNumber(policyNumberGenerator.generate());
        policy.setCustomer(customer);
        policy.setStatus(PolicyStatus.DRAFT);

        applyCreateRequest(policy, request);
        applyQuotationDefaults(policy, request.getQuotationId());
        applyProposalDefaults(policy, request.getProposalId());
        applyInsuredDefaults(policy, customer);

        if (policy.getIssuedBy() == null) {
            policy.setIssuedBy(currentUser);
        }

        if (policy.getBranch() == null && currentUser.getBranch() != null) {
            policy.setBranch(currentUser.getBranch());
        }

        replaceCoverages(policy, request.getCoverages());
        recalculateTotals(policy);

        return map(policyRepository.save(policy));
    }

    @Override
    @Transactional
    public PolicyResponseDTO updatePolicy(
            Long id,
            PolicyUpdateRequestDTO request,
            Authentication authentication) {

        currentUser(authentication);

        Policy policy = getPolicy(id);

        if (policy.getStatus() == PolicyStatus.CANCELLED
                || policy.getStatus() == PolicyStatus.EXPIRED) {
            throw new RuntimeException("Cannot update a cancelled or expired policy.");
        }

        applyUpdateRequest(policy, request);
        replaceCoverages(policy, request.getCoverages());
        recalculateTotals(policy);

        return map(policyRepository.save(policy));
    }

    @Override
    @Transactional(readOnly = true)
    public PolicyResponseDTO getPolicyById(Long id, Authentication authentication) {
        currentUser(authentication);
        return map(getPolicy(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PolicyResponseDTO getPolicyByNumber(String policyNumber, Authentication authentication) {
        currentUser(authentication);
        Policy policy = policyRepository.findByPolicyNumber(policyNumber)
                .orElseThrow(() -> new RuntimeException("Policy not found."));
        return map(policy);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<PolicyResponseDTO> getAllPolicies(
            Authentication authentication,
            Pageable pageable) {

        currentUser(authentication);

        Page<Policy> page = policyRepository.findAll(pageable);
        List<PolicyResponseDTO> content = page.getContent().stream().map(this::map).toList();

        return new PageResponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<PolicyResponseDTO> getPoliciesByCustomer(Long customerId, Authentication authentication) {
        currentUser(authentication);
        return policyRepository.findByCustomer_Id(customerId).stream().map(this::map).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PolicyResponseDTO> getPoliciesByStatus(PolicyStatus status, Authentication authentication) {
        currentUser(authentication);
        return policyRepository.findByStatus(status).stream().map(this::map).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PolicyResponseDTO> getPoliciesByType(PolicyType type, Authentication authentication) {
        currentUser(authentication);
        return policyRepository.findByPolicyType(type).stream().map(this::map).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PolicyResponseDTO> searchPolicies(String keyword, Authentication authentication) {
        currentUser(authentication);
        return policyRepository.search(keyword).stream().map(this::map).toList();
    }

    @Override
    @Transactional
    public PolicyResponseDTO issuePolicy(Long id, Authentication authentication) {
        User currentUser = currentUser(authentication);
        Policy policy = getPolicy(id);

        if (policy.getStatus() != PolicyStatus.DRAFT
                && policy.getStatus() != PolicyStatus.PENDING_ISSUANCE) {
            throw new RuntimeException("Only draft or pending policies can be issued.");
        }

        policy.setStatus(PolicyStatus.ACTIVE);
        policy.setIssueDate(LocalDate.now());
        policy.setIssuedBy(currentUser);

        if (policy.getOutstandingPremium() == null) {
            policy.setOutstandingPremium(nvl(policy.getGrandTotalPremium()));
        }

        return map(policyRepository.save(policy));
    }

    @Override
    @Transactional
    public PolicyResponseDTO suspendPolicy(Long id, Authentication authentication) {
        currentUser(authentication);
        Policy policy = getPolicy(id);

        if (policy.getStatus() != PolicyStatus.ACTIVE) {
            throw new RuntimeException("Only active policies can be suspended.");
        }

        policy.setStatus(PolicyStatus.SUSPENDED);
        return map(policyRepository.save(policy));
    }

    @Override
    @Transactional
    public PolicyResponseDTO activatePolicy(Long id, Authentication authentication) {
        currentUser(authentication);
        Policy policy = getPolicy(id);

        if (policy.getStatus() != PolicyStatus.SUSPENDED) {
            throw new RuntimeException("Only suspended policies can be activated.");
        }

        policy.setStatus(PolicyStatus.ACTIVE);
        return map(policyRepository.save(policy));
    }

    @Override
    @Transactional
    public PolicyResponseDTO cancelPolicy(Long id, String reason, Authentication authentication) {
        currentUser(authentication);
        Policy policy = getPolicy(id);

        if (policy.getStatus() == PolicyStatus.CANCELLED) {
            throw new RuntimeException("Policy is already cancelled.");
        }

        policy.setStatus(PolicyStatus.CANCELLED);
        policy.setCancellationDate(LocalDate.now());
        policy.setCancellationReason(reason);
        return map(policyRepository.save(policy));
    }

    @Override
    @Transactional
    public PolicyResponseDTO markExpired(Long id, Authentication authentication) {
        currentUser(authentication);
        Policy policy = getPolicy(id);
        policy.setStatus(PolicyStatus.EXPIRED);
        return map(policyRepository.save(policy));
    }

    @Override
    @Transactional
    public PolicyResponseDTO recordPremiumPayment(
            Long id,
            BigDecimal amount,
            Authentication authentication) {

        currentUser(authentication);

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Payment amount must be greater than zero.");
        }

        Policy policy = getPolicy(id);
        BigDecimal paid = nvl(policy.getPremiumPaid()).add(amount);
        BigDecimal total = nvl(policy.getGrandTotalPremium());
        BigDecimal outstanding = total.subtract(paid);

        if (outstanding.compareTo(BigDecimal.ZERO) < 0) {
            outstanding = BigDecimal.ZERO;
        }

        policy.setPremiumPaid(paid);
        policy.setOutstandingPremium(outstanding);

        if (outstanding.compareTo(BigDecimal.ZERO) == 0) {
            policy.setPremiumPaymentStatus(PremiumPaymentStatus.PAID);
        } else if (paid.compareTo(BigDecimal.ZERO) > 0) {
            policy.setPremiumPaymentStatus(PremiumPaymentStatus.PARTIALLY_PAID);
        }

        return map(policyRepository.save(policy));
    }

    @Override
    @Transactional
    public PolicyResponseDTO renewPolicy(
            Long id,
            PolicyRenewRequestDTO request,
            Authentication authentication) {

        currentUser(authentication);
        Policy existing = getPolicy(id);

        if (Boolean.FALSE.equals(existing.getRenewable())) {
            throw new RuntimeException("This policy is not renewable.");
        }

        Policy renewed = new Policy();
        renewed.setPolicyNumber(policyNumberGenerator.generate());
        renewed.setCustomer(existing.getCustomer());
        renewed.setQuotation(existing.getQuotation());
        renewed.setProposal(existing.getProposal());
        renewed.setBranch(existing.getBranch());
        renewed.setIssuedBy(existing.getIssuedBy());
        renewed.setServicingAgent(existing.getServicingAgent());
        renewed.setPolicyType(existing.getPolicyType());
        renewed.setProductName(existing.getProductName());
        renewed.setProductCode(existing.getProductCode());
        renewed.setStatus(PolicyStatus.DRAFT);
        renewed.setTermMonths(request.getTermMonths() != null ? request.getTermMonths() : existing.getTermMonths());
        renewed.setInceptionDate(request.getInceptionDate() != null ? request.getInceptionDate() : existing.getExpiryDate());
        renewed.setExpiryDate(request.getExpiryDate() != null
                ? request.getExpiryDate()
                : renewed.getInceptionDate().plusMonths(renewed.getTermMonths() == null ? 12 : renewed.getTermMonths()));
        renewed.setRiskLocation(existing.getRiskLocation());
        renewed.setOccupancyType(existing.getOccupancyType());
        renewed.setConstructionType(existing.getConstructionType());
        renewed.setYearBuilt(existing.getYearBuilt());
        renewed.setInsuredName(existing.getInsuredName());
        renewed.setInsuredNic(existing.getInsuredNic());
        renewed.setInsuredEmail(existing.getInsuredEmail());
        renewed.setInsuredPhone(existing.getInsuredPhone());
        renewed.setInsuredAddress(existing.getInsuredAddress());
        renewed.setNomineeName(existing.getNomineeName());
        renewed.setNomineeRelationship(existing.getNomineeRelationship());
        renewed.setCurrency(existing.getCurrency());
        renewed.setBuildingSumInsured(existing.getBuildingSumInsured());
        renewed.setContentsSumInsured(existing.getContentsSumInsured());
        renewed.setTotalSumInsured(request.getTotalSumInsured() != null
                ? request.getTotalSumInsured()
                : existing.getTotalSumInsured());
        renewed.setNetPremium(request.getNetPremium() != null ? request.getNetPremium() : existing.getNetPremium());
        renewed.setTaxAndLeviesAmount(request.getTaxAndLeviesAmount() != null
                ? request.getTaxAndLeviesAmount()
                : existing.getTaxAndLeviesAmount());
        renewed.setStampDuty(request.getStampDuty() != null ? request.getStampDuty() : existing.getStampDuty());
        renewed.setCommissionAmount(request.getCommissionAmount() != null
                ? request.getCommissionAmount()
                : existing.getCommissionAmount());
        renewed.setGrandTotalPremium(request.getGrandTotalPremium() != null
                ? request.getGrandTotalPremium()
                : existing.getGrandTotalPremium());
        renewed.setDeductible(existing.getDeductible());
        renewed.setExcessAmount(existing.getExcessAmount());
        renewed.setPaymentFrequency(existing.getPaymentFrequency());
        renewed.setPaymentMethod(existing.getPaymentMethod());
        renewed.setRenewable(true);
        renewed.setPreviousPolicyId(existing.getId());
        renewed.setRenewalCount(nvl(existing.getRenewalCount()) + 1);
        renewed.setSpecialConditions(existing.getSpecialConditions());
        renewed.setPremiumPaymentStatus(PremiumPaymentStatus.UNPAID);
        renewed.setPremiumPaid(BigDecimal.ZERO);
        renewed.setOutstandingPremium(nvl(renewed.getGrandTotalPremium()));

        for (PolicyCoverage coverage : existing.getCoverages()) {
            PolicyCoverage copy = PolicyCoverage.builder()
                    .policy(renewed)
                    .coverCode(coverage.getCoverCode())
                    .coverName(coverage.getCoverName())
                    .coverType(coverage.getCoverType())
                    .sumInsured(coverage.getSumInsured())
                    .rate(coverage.getRate())
                    .premium(coverage.getPremium())
                    .description(coverage.getDescription())
                    .optional(coverage.getOptional())
                    .build();
            renewed.getCoverages().add(copy);
        }

        existing.setStatus(PolicyStatus.RENEWED);
        existing.setRenewalDate(LocalDate.now());
        policyRepository.save(existing);

        return map(policyRepository.save(renewed));
    }

    @Override
    @Transactional
    public void deletePolicy(Long id, Authentication authentication) {
        cancelPolicy(id, "Soft deleted", authentication);
    }

    private void applyCreateRequest(Policy policy, PolicyCreateRequestDTO request) {
        policy.setPolicyType(request.getPolicyType());
        policy.setProductName(request.getProductName());
        policy.setProductCode(request.getProductCode());
        policy.setInceptionDate(request.getInceptionDate());
        policy.setExpiryDate(request.getExpiryDate());
        policy.setTermMonths(request.getTermMonths() != null ? request.getTermMonths() : 12);
        policy.setRiskLocation(request.getRiskLocation());
        policy.setOccupancyType(request.getOccupancyType());
        policy.setConstructionType(request.getConstructionType());
        policy.setYearBuilt(request.getYearBuilt());
        policy.setInsuredName(request.getInsuredName());
        policy.setInsuredNic(request.getInsuredNic());
        policy.setInsuredEmail(request.getInsuredEmail());
        policy.setInsuredPhone(request.getInsuredPhone());
        policy.setInsuredAddress(request.getInsuredAddress());
        policy.setNomineeName(request.getNomineeName());
        policy.setNomineeRelationship(request.getNomineeRelationship());
        policy.setCurrency(request.getCurrency() != null ? request.getCurrency() : CurrencyType.LKR);
        policy.setBuildingSumInsured(request.getBuildingSumInsured());
        policy.setContentsSumInsured(request.getContentsSumInsured());
        policy.setTotalSumInsured(request.getTotalSumInsured());
        policy.setNetPremium(request.getNetPremium());
        policy.setTaxAndLeviesAmount(request.getTaxAndLeviesAmount());
        policy.setStampDuty(request.getStampDuty());
        policy.setCommissionAmount(request.getCommissionAmount());
        policy.setGrandTotalPremium(request.getGrandTotalPremium());
        policy.setDeductible(request.getDeductible());
        policy.setExcessAmount(request.getExcessAmount());
        policy.setPaymentFrequency(request.getPaymentFrequency() != null
                ? request.getPaymentFrequency()
                : PaymentFrequency.ANNUAL);
        policy.setNextPremiumDueDate(request.getNextPremiumDueDate());
        policy.setPaymentMethod(request.getPaymentMethod());
        policy.setRenewable(request.getRenewable() != null ? request.getRenewable() : true);
        policy.setSpecialConditions(request.getSpecialConditions());
        policy.setRemarks(request.getRemarks());
        policy.setPremiumPaid(BigDecimal.ZERO);
        policy.setOutstandingPremium(nvl(request.getGrandTotalPremium()));
        policy.setPremiumPaymentStatus(PremiumPaymentStatus.UNPAID);

        if (policy.getInceptionDate() == null) {
            policy.setInceptionDate(LocalDate.now());
        }
        if (policy.getExpiryDate() == null) {
            policy.setExpiryDate(policy.getInceptionDate().plusMonths(policy.getTermMonths()));
        }
        if (policy.getPolicyType() == null) {
            policy.setPolicyType(PolicyType.PRIVATE_HOUSE);
        }

        if (request.getBranchCode() != null && !request.getBranchCode().isBlank()) {
            Branch branch = branchRepository.findByBranchCode(request.getBranchCode())
                    .orElseThrow(() -> new RuntimeException("Branch not found."));
            policy.setBranch(branch);
        }

        if (request.getServicingAgentEtfNo() != null && !request.getServicingAgentEtfNo().isBlank()) {
            User agent = userRepository.findById(request.getServicingAgentEtfNo())
                    .orElseThrow(() -> new RuntimeException("Servicing agent not found."));
            policy.setServicingAgent(agent);
        }
    }

    private void applyUpdateRequest(Policy policy, PolicyUpdateRequestDTO request) {
        if (request.getPolicyType() != null) {
            policy.setPolicyType(request.getPolicyType());
        }
        if (request.getProductName() != null) {
            policy.setProductName(request.getProductName());
        }
        if (request.getProductCode() != null) {
            policy.setProductCode(request.getProductCode());
        }
        if (request.getInceptionDate() != null) {
            policy.setInceptionDate(request.getInceptionDate());
        }
        if (request.getExpiryDate() != null) {
            policy.setExpiryDate(request.getExpiryDate());
        }
        if (request.getTermMonths() != null) {
            policy.setTermMonths(request.getTermMonths());
        }
        if (request.getRiskLocation() != null) {
            policy.setRiskLocation(request.getRiskLocation());
        }
        if (request.getOccupancyType() != null) {
            policy.setOccupancyType(request.getOccupancyType());
        }
        if (request.getConstructionType() != null) {
            policy.setConstructionType(request.getConstructionType());
        }
        if (request.getYearBuilt() != null) {
            policy.setYearBuilt(request.getYearBuilt());
        }
        if (request.getInsuredName() != null) {
            policy.setInsuredName(request.getInsuredName());
        }
        if (request.getInsuredNic() != null) {
            policy.setInsuredNic(request.getInsuredNic());
        }
        if (request.getInsuredEmail() != null) {
            policy.setInsuredEmail(request.getInsuredEmail());
        }
        if (request.getInsuredPhone() != null) {
            policy.setInsuredPhone(request.getInsuredPhone());
        }
        if (request.getInsuredAddress() != null) {
            policy.setInsuredAddress(request.getInsuredAddress());
        }
        if (request.getNomineeName() != null) {
            policy.setNomineeName(request.getNomineeName());
        }
        if (request.getNomineeRelationship() != null) {
            policy.setNomineeRelationship(request.getNomineeRelationship());
        }
        if (request.getCurrency() != null) {
            policy.setCurrency(request.getCurrency());
        }
        if (request.getBuildingSumInsured() != null) {
            policy.setBuildingSumInsured(request.getBuildingSumInsured());
        }
        if (request.getContentsSumInsured() != null) {
            policy.setContentsSumInsured(request.getContentsSumInsured());
        }
        if (request.getTotalSumInsured() != null) {
            policy.setTotalSumInsured(request.getTotalSumInsured());
        }
        if (request.getNetPremium() != null) {
            policy.setNetPremium(request.getNetPremium());
        }
        if (request.getTaxAndLeviesAmount() != null) {
            policy.setTaxAndLeviesAmount(request.getTaxAndLeviesAmount());
        }
        if (request.getStampDuty() != null) {
            policy.setStampDuty(request.getStampDuty());
        }
        if (request.getCommissionAmount() != null) {
            policy.setCommissionAmount(request.getCommissionAmount());
        }
        if (request.getGrandTotalPremium() != null) {
            policy.setGrandTotalPremium(request.getGrandTotalPremium());
        }
        if (request.getDeductible() != null) {
            policy.setDeductible(request.getDeductible());
        }
        if (request.getExcessAmount() != null) {
            policy.setExcessAmount(request.getExcessAmount());
        }
        if (request.getPaymentFrequency() != null) {
            policy.setPaymentFrequency(request.getPaymentFrequency());
        }
        if (request.getNextPremiumDueDate() != null) {
            policy.setNextPremiumDueDate(request.getNextPremiumDueDate());
        }
        if (request.getPaymentMethod() != null) {
            policy.setPaymentMethod(request.getPaymentMethod());
        }
        if (request.getRenewable() != null) {
            policy.setRenewable(request.getRenewable());
        }
        if (request.getSpecialConditions() != null) {
            policy.setSpecialConditions(request.getSpecialConditions());
        }
        if (request.getRemarks() != null) {
            policy.setRemarks(request.getRemarks());
        }
        if (request.getBranchCode() != null && !request.getBranchCode().isBlank()) {
            Branch branch = branchRepository.findByBranchCode(request.getBranchCode())
                    .orElseThrow(() -> new RuntimeException("Branch not found."));
            policy.setBranch(branch);
        }
        if (request.getServicingAgentEtfNo() != null && !request.getServicingAgentEtfNo().isBlank()) {
            User agent = userRepository.findById(request.getServicingAgentEtfNo())
                    .orElseThrow(() -> new RuntimeException("Servicing agent not found."));
            policy.setServicingAgent(agent);
        }
    }

    private void applyQuotationDefaults(Policy policy, Long quotationId) {
        if (quotationId == null) {
            return;
        }

        Quotation quotation = quotationRepository.findById(quotationId)
                .orElseThrow(() -> new RuntimeException("Quotation not found."));

        policy.setQuotation(quotation);

        if (policy.getPolicyType() == null) {
            policy.setPolicyType(mapQuotationType(quotation.getQuotationType()));
        }
        if (policy.getProductName() == null) {
            policy.setProductName(quotation.getQuotationType() != null
                    ? quotation.getQuotationType().name()
                    : null);
        }
        if (policy.getCurrency() == null) {
            policy.setCurrency(quotation.getCurrency());
        }
        if (policy.getTotalSumInsured() == null) {
            policy.setTotalSumInsured(quotation.getTotalSumInsured());
        }
        if (policy.getNetPremium() == null) {
            policy.setNetPremium(quotation.getNetPremium());
        }
        if (policy.getTaxAndLeviesAmount() == null) {
            policy.setTaxAndLeviesAmount(quotation.getTaxAndLeviesAmount());
        }
        if (policy.getGrandTotalPremium() == null) {
            policy.setGrandTotalPremium(quotation.getGrandTotalPremium());
        }
        if (policy.getInsuredName() == null) {
            policy.setInsuredName(quotation.getCustomerName());
        }
    }

    private void applyProposalDefaults(Policy policy, Long proposalId) {
        if (proposalId == null) {
            return;
        }

        Proposal proposal = proposalRepository.findById(proposalId)
                .orElseThrow(() -> new RuntimeException("Proposal not found."));

        policy.setProposal(proposal);

        if (policy.getInsuredName() == null) {
            policy.setInsuredName(proposal.getCustomerName());
        }
        if (policy.getInsuredEmail() == null) {
            policy.setInsuredEmail(proposal.getCustomerEmail());
        }
        if (policy.getInsuredPhone() == null) {
            policy.setInsuredPhone(proposal.getCustomerPhone());
        }
        if (policy.getProductName() == null) {
            policy.setProductName(proposal.getProductName());
        }
    }

    private void applyInsuredDefaults(Policy policy, Customer customer) {
        if (policy.getInsuredEmail() == null) {
            policy.setInsuredEmail(customer.getEmail());
        }
        if (policy.getInsuredPhone() == null) {
            policy.setInsuredPhone(customer.getPhoneNumber());
        }
        if (policy.getInsuredAddress() == null) {
            policy.setInsuredAddress(customer.getAddress());
        }
    }

    private void replaceCoverages(Policy policy, List<PolicyCoverageDTO> coverageDtos) {
        policy.getCoverages().clear();

        if (coverageDtos == null) {
            return;
        }

        for (PolicyCoverageDTO dto : coverageDtos) {
            if (dto.getCoverType() == null) {
                throw new RuntimeException("Coverage type is required.");
            }

            PolicyCoverage coverage = PolicyCoverage.builder()
                    .policy(policy)
                    .coverCode(dto.getCoverCode())
                    .coverName(dto.getCoverName() != null ? dto.getCoverName() : dto.getCoverType().name())
                    .coverType(dto.getCoverType())
                    .sumInsured(dto.getSumInsured())
                    .rate(dto.getRate())
                    .premium(dto.getPremium())
                    .description(dto.getDescription())
                    .optional(dto.getOptional() != null ? dto.getOptional() : false)
                    .build();
            policy.getCoverages().add(coverage);
        }
    }

    private void recalculateTotals(Policy policy) {
        BigDecimal coverageSum = policy.getCoverages().stream()
                .map(c -> nvl(c.getSumInsured()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal coveragePremium = policy.getCoverages().stream()
                .map(c -> nvl(c.getPremium()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (policy.getTotalSumInsured() == null || policy.getTotalSumInsured().compareTo(BigDecimal.ZERO) == 0) {
            BigDecimal buildingAndContents = nvl(policy.getBuildingSumInsured()).add(nvl(policy.getContentsSumInsured()));
            policy.setTotalSumInsured(buildingAndContents.compareTo(BigDecimal.ZERO) > 0
                    ? buildingAndContents
                    : coverageSum);
        }

        if (policy.getNetPremium() == null || policy.getNetPremium().compareTo(BigDecimal.ZERO) == 0) {
            if (coveragePremium.compareTo(BigDecimal.ZERO) > 0) {
                policy.setNetPremium(coveragePremium);
            }
        }

        if (policy.getGrandTotalPremium() == null) {
            policy.setGrandTotalPremium(
                    nvl(policy.getNetPremium())
                            .add(nvl(policy.getTaxAndLeviesAmount()))
                            .add(nvl(policy.getStampDuty()))
            );
        }

        policy.setOutstandingPremium(nvl(policy.getGrandTotalPremium()).subtract(nvl(policy.getPremiumPaid())));
    }

    private PolicyType mapQuotationType(QuotationType quotationType) {
        if (quotationType == null) {
            return PolicyType.PRIVATE_HOUSE;
        }
        return switch (quotationType) {
            case PRIVATE_HOUSE -> PolicyType.PRIVATE_HOUSE;
            case BUSINESS_PREMISES -> PolicyType.BUSINESS_PREMISES;
            case INDUSTRIAL_PREMISES -> PolicyType.INDUSTRIAL_PREMISES;
        };
    }

    private Policy getPolicy(Long id) {
        return policyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Policy not found."));
    }

    private User currentUser(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new RuntimeException("Unauthenticated request.");
        }
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found."));
    }

    private BigDecimal nvl(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private Integer nvl(Integer value) {
        return value == null ? 0 : value;
    }

    private PolicyResponseDTO map(Policy policy) {
        PolicyResponseDTO dto = new PolicyResponseDTO();
        dto.setId(policy.getId());
        dto.setPolicyNumber(policy.getPolicyNumber());
        dto.setPolicyVersion(policy.getPolicyVersion());
        dto.setEndorsementNumber(policy.getEndorsementNumber());

        if (policy.getCustomer() != null) {
            dto.setCustomerId(policy.getCustomer().getId());
            dto.setCustomerCode(policy.getCustomer().getCustomerCode());
        }
        if (policy.getQuotation() != null) {
            dto.setQuotationId(policy.getQuotation().getId());
            dto.setQuotationReference(policy.getQuotation().getQuotationReference());
        }
        if (policy.getProposal() != null) {
            dto.setProposalId(policy.getProposal().getId());
            dto.setProposalNumber(policy.getProposal().getProposalNumber());
        }
        if (policy.getBranch() != null) {
            dto.setBranchCode(policy.getBranch().getBranchCode());
            dto.setBranchName(policy.getBranch().getBranchName());
        }
        if (policy.getIssuedBy() != null) {
            dto.setIssuedByEtfNo(policy.getIssuedBy().getEtfNo());
        }
        if (policy.getServicingAgent() != null) {
            dto.setServicingAgentEtfNo(policy.getServicingAgent().getEtfNo());
        }

        dto.setPolicyType(policy.getPolicyType());
        dto.setProductName(policy.getProductName());
        dto.setProductCode(policy.getProductCode());
        dto.setStatus(policy.getStatus());
        dto.setInceptionDate(policy.getInceptionDate());
        dto.setExpiryDate(policy.getExpiryDate());
        dto.setIssueDate(policy.getIssueDate());
        dto.setRenewalDate(policy.getRenewalDate());
        dto.setCancellationDate(policy.getCancellationDate());
        dto.setTermMonths(policy.getTermMonths());
        dto.setRiskLocation(policy.getRiskLocation());
        dto.setOccupancyType(policy.getOccupancyType());
        dto.setConstructionType(policy.getConstructionType());
        dto.setYearBuilt(policy.getYearBuilt());
        dto.setInsuredName(policy.getInsuredName());
        dto.setInsuredNic(policy.getInsuredNic());
        dto.setInsuredEmail(policy.getInsuredEmail());
        dto.setInsuredPhone(policy.getInsuredPhone());
        dto.setInsuredAddress(policy.getInsuredAddress());
        dto.setNomineeName(policy.getNomineeName());
        dto.setNomineeRelationship(policy.getNomineeRelationship());
        dto.setCurrency(policy.getCurrency());
        dto.setBuildingSumInsured(policy.getBuildingSumInsured());
        dto.setContentsSumInsured(policy.getContentsSumInsured());
        dto.setTotalSumInsured(policy.getTotalSumInsured());
        dto.setNetPremium(policy.getNetPremium());
        dto.setTaxAndLeviesAmount(policy.getTaxAndLeviesAmount());
        dto.setStampDuty(policy.getStampDuty());
        dto.setCommissionAmount(policy.getCommissionAmount());
        dto.setGrandTotalPremium(policy.getGrandTotalPremium());
        dto.setDeductible(policy.getDeductible());
        dto.setExcessAmount(policy.getExcessAmount());
        dto.setPaymentFrequency(policy.getPaymentFrequency());
        dto.setPremiumPaymentStatus(policy.getPremiumPaymentStatus());
        dto.setPremiumPaid(policy.getPremiumPaid());
        dto.setOutstandingPremium(policy.getOutstandingPremium());
        dto.setNextPremiumDueDate(policy.getNextPremiumDueDate());
        dto.setPaymentMethod(policy.getPaymentMethod());
        dto.setCancellationReason(policy.getCancellationReason());
        dto.setLapseReason(policy.getLapseReason());
        dto.setRenewable(policy.getRenewable());
        dto.setPreviousPolicyId(policy.getPreviousPolicyId());
        dto.setRenewalCount(policy.getRenewalCount());
        dto.setSpecialConditions(policy.getSpecialConditions());
        dto.setRemarks(policy.getRemarks());
        dto.setCreatedAt(policy.getCreatedAt());
        dto.setUpdatedAt(policy.getUpdatedAt());

        dto.setCoverages(policy.getCoverages().stream().map(this::mapCoverage).toList());
        return dto;
    }

    private PolicyCoverageDTO mapCoverage(PolicyCoverage coverage) {
        PolicyCoverageDTO dto = new PolicyCoverageDTO();
        dto.setId(coverage.getId());
        dto.setCoverCode(coverage.getCoverCode());
        dto.setCoverName(coverage.getCoverName());
        dto.setCoverType(coverage.getCoverType());
        dto.setSumInsured(coverage.getSumInsured());
        dto.setRate(coverage.getRate());
        dto.setPremium(coverage.getPremium());
        dto.setDescription(coverage.getDescription());
        dto.setOptional(coverage.getOptional());
        return dto;
    }
}
