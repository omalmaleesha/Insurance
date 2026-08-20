package com.example.Insurance.services.impl;

import com.example.Insurance.dto.qty.*;
import com.example.Insurance.entities.User;
import com.example.Insurance.entities.qty.*;
import com.example.Insurance.repository.UserRepository;
import com.example.Insurance.repository.qty.CommercialRiskRepository;
import com.example.Insurance.repository.qty.IndustrialRiskRepository;
import com.example.Insurance.repository.qty.QuotationRepository;
import com.example.Insurance.repository.qty.ResidentialRiskRepository;
import com.example.Insurance.services.PremiumCalculationService;
import com.example.Insurance.services.QuotationPdfService;
import com.example.Insurance.services.QuotationService;
import com.example.Insurance.utils.types.QuotationStatus;
import com.example.Insurance.utils.types.RiskEntityType;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuotationServiceImpl implements QuotationService {

    private final QuotationRepository quotationRepository;

    private final ResidentialRiskRepository residentialRiskRepository;

    private final CommercialRiskRepository commercialRiskRepository;

    private final IndustrialRiskRepository industrialRiskRepository;

    private final PremiumCalculationService premiumCalculationService;

    private final UserRepository userRepository;

    private final QuotationPdfService quotationPdfService;


    @Override
    @Transactional
    public QuotationResponse createQuotation(
            CreateQuotationRequest request,
            Authentication authentication) {

        // Logged user
        User user = userRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        // Validation
        if (request.getCustomerName() == null ||
                request.getCustomerName().isBlank()) {

            throw new RuntimeException("Customer name is required");
        }

        // Create quotation
        Quotation quotation = new Quotation();

        quotation.setQuotationReference(generateReference());

        quotation.setCustomerName(request.getCustomerName());

        quotation.setCurrency(request.getCurrency());

        quotation.setQuotationType(request.getQuotationType());

        quotation.setStatus(QuotationStatus.DRAFT);

        AbstractRisk risk;

        switch (request.getQuotationType()) {

            case PRIVATE_HOUSE -> {

                ResidentialRisk residentialRisk =
                        mapResidential(request.getResidentialRisk());

                residentialRisk.setQuotation(quotation);

                risk = residentialRisk;

            }

            case BUSINESS_PREMISES -> {

                CommercialRisk commercialRisk =
                        mapCommercial(request.getCommercialRisk());

                commercialRisk.setQuotation(quotation);

                risk = commercialRisk;

            }

            case INDUSTRIAL_PREMISES -> {

                IndustrialRisk industrialRisk =
                        mapIndustrial(request.getIndustrialRisk());

                industrialRisk.setQuotation(quotation);

                risk = industrialRisk;

            }

            default -> throw new RuntimeException("Unsupported quotation type");
        }

        quotation.getRisks().add(risk);

        premiumCalculationService.calculateQuotation(
                quotation,
                risk
        );

        quotationRepository.save(quotation);

        return mapResponse(quotation);

    }

    private String generateReference() {

        return "QT-"
                + System.currentTimeMillis();

    }

    private QuotationResponse mapResponse(
            Quotation quotation) {

        return QuotationResponse.builder()

                .id(quotation.getId())

                .quotationReference(
                        quotation.getQuotationReference())

                .customerName(
                        quotation.getCustomerName())

                .currency(
                        quotation.getCurrency())

                .quotationType(
                        quotation.getQuotationType())

                .status(
                        quotation.getStatus())

                .totalSumInsured(
                        quotation.getTotalSumInsured())

                .netPremium(
                        quotation.getNetPremium())

                .taxAndLeviesAmount(
                        quotation.getTaxAndLeviesAmount())

                .grandTotalPremium(
                        quotation.getGrandTotalPremium())

                .build();

    }

    private ResidentialRisk mapResidential(
            ResidentialRiskRequest request) {

        ResidentialRisk risk = new ResidentialRisk();

        risk.setEntityType(RiskEntityType.RESIDENTIAL);

        risk.setPropertyName(request.getPropertyName());

        risk.setLocationAddress(request.getLocationAddress());

        risk.setBuildingSumInsured(request.getBuildingSumInsured());

        risk.setContentsSumInsured(request.getContentsSumInsured());

        risk.setLoadingPercentage(request.getLoadingPercentage());

        risk.setDiscountPercentage(request.getDiscountPercentage());

        risk.setHasFireAlarm(request.getHasFireAlarm());

        risk.setHasAutomaticSprinklers(
                request.getHasAutomaticSprinklers());

        risk.setDistanceToNearestFireStationKm(
                request.getDistanceToNearestFireStationKm());

        risk.setConstructionMaterialClass(
                request.getConstructionMaterialClass());

        risk.setNumberOfFloors(
                request.getNumberOfFloors());

        risk.setYearBuilt(
                request.getYearBuilt());

        risk.setOccupancyType(
                request.getOccupancyType());

        risk.setSecuredGatedProperty(
                request.getIsSecuredGatedProperty());

        return risk;

    }

    @Override
    @Transactional(readOnly = true)
    public QuotationResponse getQuotation(
            Long id,
            Authentication authentication) {

        // Validate logged in user
        User user = userRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        // Find quotation
        Quotation quotation = quotationRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Quotation not found"));

        // Optional permission check
        // Example:
        // if (!quotation.getCreatedBy().getEtfNo().equals(user.getEtfNo())) {
        //     throw new RuntimeException("Access denied");
        // }

        return mapResponse(quotation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuotationResponse> getAllQuotations(
            Authentication authentication) {

        // Validate logged-in user
        userRepository.findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        return quotationRepository.findAll()
                .stream()
                .map(this::mapResponse)
                .toList();
    }

    @Override
    @Transactional
    public QuotationResponse updateQuotation(
            Long id,
            CreateQuotationRequest request,
            Authentication authentication) {

        // Validate logged-in user
        userRepository.findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Quotation quotation = quotationRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Quotation not found"));

        // Allow editing only while draft
        if (quotation.getStatus() != QuotationStatus.DRAFT) {
            throw new RuntimeException(
                    "Only Draft quotations can be updated.");
        }

        quotation.setCustomerName(request.getCustomerName());
        quotation.setCurrency(request.getCurrency());
        quotation.setQuotationType(request.getQuotationType());

        quotation.getRisks().clear();

        AbstractRisk risk;

        switch (request.getQuotationType()) {

            case PRIVATE_HOUSE -> {

                ResidentialRisk residentialRisk =
                        mapResidential(request.getResidentialRisk());

                residentialRisk.setQuotation(quotation);

                risk = residentialRisk;
            }

            case BUSINESS_PREMISES -> {

                CommercialRisk commercialRisk =
                        mapCommercial(request.getCommercialRisk());

                commercialRisk.setQuotation(quotation);

                risk = commercialRisk;
            }

            case INDUSTRIAL_PREMISES -> {

                IndustrialRisk industrialRisk =
                        mapIndustrial(request.getIndustrialRisk());

                industrialRisk.setQuotation(quotation);

                risk = industrialRisk;
            }

            default ->
                    throw new RuntimeException("Unsupported quotation type");
        }

        quotation.getRisks().add(risk);

        premiumCalculationService.calculateQuotation(
                quotation,
                risk
        );

        quotationRepository.save(quotation);

        return mapResponse(quotation);

    }

    private IndustrialRisk mapIndustrial(IndustrialRiskRequest industrialRisk) {

        IndustrialRisk risk = new IndustrialRisk();

        risk.setEntityType(RiskEntityType.INDUSTRIAL);

        risk.setManufacturingType(industrialRisk.getManufacturingType());

        risk.setMachinerySumInsured(industrialRisk.getMachinerySumInsured());

        risk.setContentsSumInsured(industrialRisk.getContentsSumInsured());

        risk.setFlammableMaterials(industrialRisk.getHasFlammableMaterials());

        risk.setFlammableMaterialCategory(industrialRisk.getFlammableMaterialCategory());

        risk.setBoilersOrHeavyMachinery(industrialRisk.getHasBoilersOrHeavyMachinery());

        risk.setFireDoorsAndWalls(industrialRisk.getHasFireDoorsAndWalls());

        risk.setOnsiteHydrants(industrialRisk.getHasOnsiteHydrants());

        return risk;
    }

    private CommercialRisk mapCommercial(CommercialRiskRequest commercialRisk) {

        CommercialRisk risk = new CommercialRisk();

        risk.setEntityType(RiskEntityType.COMMERCIAL);

        risk.setBusinessActivity(commercialRisk.getBusinessActivity());

        risk.setFootTrafficLevel(commercialRisk.getFootTrafficLevel());

        risk.setBuildingSumInsured(commercialRisk.getBuildingSumInsured());

        risk.setContentsSumInsured(commercialRisk.getContentsSumInsured());

        risk.setServerRoom(commercialRisk.getHasServerRoom());

        risk.setKitchenOrCookingFacility(commercialRisk.getHasKitchenOrCookingFacility());

        risk.setHoseReels(commercialRisk.getHasHoseReels());

        return risk;
    }

    @Override
    @Transactional
    public void deleteQuotation(
            Long id,
            Authentication authentication) {

        // Validate logged-in user
        userRepository.findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Quotation quotation = quotationRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Quotation not found"));

        // Prevent deleting approved/issued quotations
        if (quotation.getStatus() == QuotationStatus.APPROVED
                || quotation.getStatus() == QuotationStatus.ISSUED) {

            throw new RuntimeException(
                    "Approved or Issued quotations cannot be deleted.");
        }

        quotationRepository.delete(quotation);

    }

    @Override
    @Transactional
    public QuotationResponse calculatePremium(
            Long id,
            Authentication authentication) {

        // Validate logged-in user
        userRepository.findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Quotation quotation = quotationRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Quotation not found"));

        if (quotation.getRisks().isEmpty()) {
            throw new RuntimeException("No risk found.");
        }

        AbstractRisk risk = quotation.getRisks().get(0);

        premiumCalculationService.calculateQuotation(
                quotation,
                risk
        );

        quotation.setStatus(QuotationStatus.CALCULATED);

        quotationRepository.save(quotation);

        return mapResponse(quotation);
    }

    @Override
    @Transactional
    public QuotationResponse approveQuotation(
            Long id,
            Authentication authentication) {

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Quotation quotation = quotationRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Quotation not found"));

        if (quotation.getStatus() != QuotationStatus.CALCULATED) {
            throw new RuntimeException(
                    "Quotation must be calculated before approval.");
        }

        quotation.setStatus(QuotationStatus.APPROVED);

        quotationRepository.save(quotation);

        return mapResponse(quotation);

    }
    @Override
    @Transactional
    public QuotationResponse rejectQuotation(
            Long id,
            String reason,
            Authentication authentication) {

        userRepository.findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Quotation quotation = quotationRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Quotation not found"));

        quotation.setStatus(QuotationStatus.REJECTED);

        // Later save rejection reason

        quotationRepository.save(quotation);

        return mapResponse(quotation);

    }

    @Override
    @Transactional
    public QuotationResponse issueQuotation(
            Long id,
            Authentication authentication) {

        userRepository.findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Quotation quotation = quotationRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Quotation not found"));

        if (quotation.getStatus() != QuotationStatus.APPROVED) {
            throw new RuntimeException(
                    "Only approved quotations can be issued.");
        }

        quotation.setStatus(QuotationStatus.ISSUED);

        quotationRepository.save(quotation);

        return mapResponse(quotation);

    }
    @Override
    @Transactional
    public QuotationResponse duplicateQuotation(
            Long id,
            Authentication authentication) {

        userRepository.findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Quotation oldQuotation = quotationRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Quotation not found"));

        Quotation quotation = new Quotation();

        quotation.setQuotationReference(generateReference());

        quotation.setCustomerName(oldQuotation.getCustomerName());

        quotation.setCurrency(oldQuotation.getCurrency());

        quotation.setQuotationType(oldQuotation.getQuotationType());

        quotation.setStatus(QuotationStatus.DRAFT);

        AbstractRisk oldRisk = oldQuotation.getRisks().get(0);

        AbstractRisk newRisk;

        if (oldRisk instanceof ResidentialRisk residential) {

            ResidentialRisk copy = new ResidentialRisk();

            BeanUtils.copyProperties(residential, copy, "id", "quotation");

            copy.setQuotation(quotation);

            newRisk = copy;

        } else if (oldRisk instanceof CommercialRisk commercial) {

            CommercialRisk copy = new CommercialRisk();

            BeanUtils.copyProperties(commercial, copy, "id", "quotation");

            copy.setQuotation(quotation);

            newRisk = copy;

        } else {

            IndustrialRisk industrial =
                    (IndustrialRisk) oldRisk;

            IndustrialRisk copy = new IndustrialRisk();

            BeanUtils.copyProperties(industrial, copy, "id", "quotation");

            copy.setQuotation(quotation);

            newRisk = copy;

        }

        quotation.getRisks().add(newRisk);

        premiumCalculationService.calculateQuotation(
                quotation,
                newRisk
        );

        quotationRepository.save(quotation);

        return mapResponse(quotation);

    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> downloadQuotationPdf(
            Long id,
            Authentication authentication) {

        userRepository.findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Quotation quotation = quotationRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Quotation not found"));

        byte[] pdf = quotationPdfService.generateQuotationPdf(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename="
                                + quotation.getQuotationReference()
                                + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);

    }


}

