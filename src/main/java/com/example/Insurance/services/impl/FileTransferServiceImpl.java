package com.example.Insurance.services.impl;

import com.example.Insurance.dto.delivery.DeliveryConfirmationRequest;
import com.example.Insurance.dto.delivery.DeliveryConfirmationResponse;
import com.example.Insurance.dto.shipment.CreateShipmentRequest;
import com.example.Insurance.dto.shipment.DispatchShipmentRequest;
import com.example.Insurance.dto.shipment.ShipmentResponse;
import com.example.Insurance.dto.shipmentitem.ShipmentItemRequest;
import com.example.Insurance.dto.shipmentitem.ShipmentItemResponse;
import com.example.Insurance.entities.*;
import com.example.Insurance.entities.fileTrasfer.Branch;
import com.example.Insurance.entities.fileTrasfer.CourierShipment;
import com.example.Insurance.entities.fileTrasfer.DeliveryConfirmation;
import com.example.Insurance.entities.fileTrasfer.ShipmentItem;
import com.example.Insurance.repository.*;
import com.example.Insurance.repository.fileTrasfer.BranchRepository;
import com.example.Insurance.repository.fileTrasfer.CourierShipmentRepository;
import com.example.Insurance.repository.fileTrasfer.DeliveryConfirmationRepository;
import com.example.Insurance.repository.fileTrasfer.ShipmentItemRepository;
import com.example.Insurance.services.FileTransferService;
import com.example.Insurance.utils.types.ShipmentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileTransferServiceImpl implements FileTransferService {

    private final CourierShipmentRepository shipmentRepository;
    private final ShipmentItemRepository shipmentItemRepository;
    private final BranchRepository branchRepository;
    private final DeliveryConfirmationRepository confirmationRepository;
    private final UserRepository userRepository;
    private final UserInfoRepository userInfoRepository;


    private User getLoggedUser(Authentication authentication) {

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Authenticated user not found"));
    }

    private Branch getUserBranch(User user) {

        return branchRepository.findByBranchCode(user.getBranchCode())
                .orElseThrow(() ->
                        new RuntimeException("Branch not found"));
    }

    private CourierShipment getShipmentEntity(Long shipmentId) {

        return shipmentRepository.findById(shipmentId)
                .orElseThrow(() ->
                        new RuntimeException("Shipment not found"));
    }


    @Override
    public ShipmentResponse createShipment(CreateShipmentRequest request,
                                           Authentication authentication) {

        User user = getLoggedUser(authentication);

        Branch fromBranch = branchRepository.findByBranchCode(String.valueOf(request.getFromBranchId()))
                .orElseThrow(() ->
                        new RuntimeException("From branch not found"));

        Branch toBranch = branchRepository.findByBranchCode(String.valueOf(request.getToBranchId()))
                .orElseThrow(() ->
                        new RuntimeException("Destination branch not found"));

        CourierShipment shipment = CourierShipment.builder()
                .trackingNumber("TRK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .fromBranch(fromBranch)
                .toBranch(toBranch)
                .sentBy(user)
                .courierCompany(request.getCourierCompany())
                .courierReference(request.getCourierReference())
                .remarks(request.getRemarks())
                .status(ShipmentStatus.CREATED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        shipmentRepository.save(shipment);

        return ShipmentResponse.builder()
                .id(shipment.getId())
                .trackingNumber(shipment.getTrackingNumber())
                .fromBranch(fromBranch.getBranchName())
                .toBranch(toBranch.getBranchName())
                .sentBy(user.getFirstName() + " " + user.getLastName())
                .courierCompany(shipment.getCourierCompany())
                .courierReference(shipment.getCourierReference())
                .remarks(shipment.getRemarks())
                .status(shipment.getStatus())
                .build();
    }


    @Override
    public ShipmentItemResponse addItem(Long shipmentId,
                                        ShipmentItemRequest request,
                                        Authentication authentication) {

        CourierShipment shipment = getShipmentEntity(shipmentId);

        if (shipment.getStatus() != ShipmentStatus.CREATED) {
            throw new RuntimeException(
                    "Cannot add documents after shipment is dispatched.");
        }

        ShipmentItem item = ShipmentItem.builder()
                .shipment(shipment)
                .documentType(request.getDocumentType())
                .documentNumber(request.getDocumentNumber())
                .customerName(request.getCustomerName())
                .policyNumber(request.getPolicyNumber())
                .pageCount(request.getPageCount())
                .remarks(request.getRemarks())
                .received(false)
                .build();

        shipmentItemRepository.save(item);

        return ShipmentItemResponse.builder()
                .id(item.getId())
                .documentType(item.getDocumentType())
                .documentNumber(item.getDocumentNumber())
                .customerName(item.getCustomerName())
                .policyNumber(item.getPolicyNumber())
                .pageCount(item.getPageCount())
                .received(item.getReceived())
                .build();
    }


    @Override
    public List<ShipmentItemResponse> getItems(Long shipmentId) {

        CourierShipment shipment = getShipmentEntity(shipmentId);

        return shipmentItemRepository.findByShipment(shipment)
                .stream()
                .map(item -> ShipmentItemResponse.builder()
                        .id(item.getId())
                        .documentType(item.getDocumentType())
                        .documentNumber(item.getDocumentNumber())
                        .customerName(item.getCustomerName())
                        .policyNumber(item.getPolicyNumber())
                        .pageCount(item.getPageCount())
                        .received(item.getReceived())
                        .build())
                .toList();
    }

    @Override
    public ShipmentResponse dispatchShipment(Long shipmentId,
                                             DispatchShipmentRequest request,
                                             Authentication authentication) {

        User user = getLoggedUser(authentication);

        CourierShipment shipment = getShipmentEntity(shipmentId);

        if (shipment.getStatus() != ShipmentStatus.CREATED) {
            throw new RuntimeException("Shipment has already been dispatched.");
        }

        long totalItems = shipmentItemRepository.countByShipment(shipment);

        if (totalItems == 0) {
            throw new RuntimeException("Cannot dispatch an empty shipment.");
        }

        shipment.setCourierCompany(request.getCourierCompany());
        shipment.setCourierReference(request.getCourierReference());
        shipment.setStatus(ShipmentStatus.DISPATCHED);
        shipment.setSentDate(LocalDateTime.now());
        shipment.setUpdatedAt(LocalDateTime.now());

        shipmentRepository.save(shipment);

        return ShipmentResponse.builder()
                .id(shipment.getId())
                .trackingNumber(shipment.getTrackingNumber())
                .fromBranch(shipment.getFromBranch().getBranchName())
                .toBranch(shipment.getToBranch().getBranchName())
                .sentBy(user.getFirstName() + " " + user.getLastName())
                .sentDate(shipment.getSentDate())
                .courierCompany(shipment.getCourierCompany())
                .courierReference(shipment.getCourierReference())
                .remarks(shipment.getRemarks())
                .status(shipment.getStatus())
                .build();
    }


    @Override
    public ShipmentResponse getShipment(Long shipmentId) {

        CourierShipment shipment = getShipmentEntity(shipmentId);

        return ShipmentResponse.builder()
                .id(shipment.getId())
                .trackingNumber(shipment.getTrackingNumber())
                .fromBranch(shipment.getFromBranch().getBranchName())
                .toBranch(shipment.getToBranch().getBranchName())
                .sentBy(shipment.getSentBy().getFirstName() + " " +
                        shipment.getSentBy().getLastName())
                .sentDate(shipment.getSentDate())
                .courierCompany(shipment.getCourierCompany())
                .courierReference(shipment.getCourierReference())
                .remarks(shipment.getRemarks())
                .status(shipment.getStatus())
                .build();
    }


    @Override
    public List<ShipmentResponse> getIncomingShipments(Authentication authentication) {

        User user = getLoggedUser(authentication);

        Branch branch = getUserBranch(user);

        return shipmentRepository
                .findByToBranchAndStatus(branch, ShipmentStatus.DISPATCHED)
                .stream()
                .map(shipment -> ShipmentResponse.builder()
                        .id(shipment.getId())
                        .trackingNumber(shipment.getTrackingNumber())
                        .fromBranch(shipment.getFromBranch().getBranchName())
                        .toBranch(shipment.getToBranch().getBranchName())
                        .sentBy(shipment.getSentBy().getFirstName() + " " +
                                shipment.getSentBy().getLastName())
                        .sentDate(shipment.getSentDate())
                        .courierCompany(shipment.getCourierCompany())
                        .courierReference(shipment.getCourierReference())
                        .remarks(shipment.getRemarks())
                        .status(shipment.getStatus())
                        .build())
                .toList();
    }


    @Override
    public List<ShipmentResponse> getOutgoingShipments(Authentication authentication) {

        User user = getLoggedUser(authentication);

        Branch branch = getUserBranch(user);

        return shipmentRepository.findByFromBranch(branch)
                .stream()
                .map(shipment -> ShipmentResponse.builder()
                        .id(shipment.getId())
                        .trackingNumber(shipment.getTrackingNumber())
                        .fromBranch(shipment.getFromBranch().getBranchName())
                        .toBranch(shipment.getToBranch().getBranchName())
                        .sentBy(shipment.getSentBy().getFirstName() + " " +
                                shipment.getSentBy().getLastName())
                        .sentDate(shipment.getSentDate())
                        .courierCompany(shipment.getCourierCompany())
                        .courierReference(shipment.getCourierReference())
                        .remarks(shipment.getRemarks())
                        .status(shipment.getStatus())
                        .build())
                .toList();
    }

    @Override
    public DeliveryConfirmationResponse receiveShipment(Long shipmentId,
                                                        DeliveryConfirmationRequest request,
                                                        Authentication authentication) {

        User user = getLoggedUser(authentication);

        CourierShipment shipment = getShipmentEntity(shipmentId);

        if (shipment.getStatus() != ShipmentStatus.DISPATCHED) {
            throw new RuntimeException("Shipment is not available for receiving.");
        }

        Branch userBranch = getUserBranch(user);

        if (!shipment.getToBranch().getId().equals(userBranch.getId())) {
            throw new RuntimeException("You cannot receive shipments for another branch.");
        }

        DeliveryConfirmation confirmation = DeliveryConfirmation.builder()
                .shipment(shipment)
                .receivedBy(user)
                .receivedDate(LocalDateTime.now())
                .condition(request.getCondition())
                .remarks(request.getRemarks())
                .build();

        confirmationRepository.save(confirmation);

        List<ShipmentItem> items = shipmentItemRepository.findByShipment(shipment);

        for (ShipmentItem item : items) {
            item.setReceived(true);
            item.setReceivedDate(LocalDateTime.now());
        }

        shipmentItemRepository.saveAll(items);

        shipment.setStatus(ShipmentStatus.RECEIVED);
        shipment.setUpdatedAt(LocalDateTime.now());

        shipmentRepository.save(shipment);

        return DeliveryConfirmationResponse.builder()
                .id(confirmation.getId())
                .receivedBy(user.getFirstName() + " " + user.getLastName())
                .receivedDate(confirmation.getReceivedDate())
                .condition(confirmation.getCondition())
                .remarks(confirmation.getRemarks())
                .build();
    }


    @Override
    public List<ShipmentResponse> getHistory(Authentication authentication) {

        User user = getLoggedUser(authentication);

        Branch branch = getUserBranch(user);

        List<CourierShipment> outgoing =
                shipmentRepository.findByFromBranch(branch);

        List<CourierShipment> incoming =
                shipmentRepository.findByToBranch(branch);

        List<CourierShipment> history = new ArrayList<>();

        history.addAll(outgoing);
        history.addAll(incoming);

        history.sort((a, b) ->
                b.getCreatedAt().compareTo(a.getCreatedAt()));

        return history.stream()
                .map(this::mapShipmentResponse)
                .toList();
    }

    @Override
    public void cancelShipment(Long shipmentId,
                               Authentication authentication) {

        User user = getLoggedUser(authentication);

        CourierShipment shipment = getShipmentEntity(shipmentId);

        if (!shipment.getSentBy().getEtfNo().equals(user.getEtfNo())) {
            throw new RuntimeException("Only the creator can cancel this shipment.");
        }

        if (shipment.getStatus() != ShipmentStatus.CREATED) {
            throw new RuntimeException(
                    "Only shipments in CREATED status can be cancelled.");
        }

        shipment.setStatus(ShipmentStatus.CANCELLED);
        shipment.setUpdatedAt(LocalDateTime.now());

        shipmentRepository.save(shipment);
    }

    private ShipmentResponse mapShipmentResponse(CourierShipment shipment) {

        return ShipmentResponse.builder()
                .id(shipment.getId())
                .trackingNumber(shipment.getTrackingNumber())
                .fromBranch(shipment.getFromBranch().getBranchName())
                .toBranch(shipment.getToBranch().getBranchName())
                .sentBy(shipment.getSentBy().getFirstName() + " "
                        + shipment.getSentBy().getLastName())
                .sentDate(shipment.getSentDate())
                .courierCompany(shipment.getCourierCompany())
                .courierReference(shipment.getCourierReference())
                .remarks(shipment.getRemarks())
                .status(shipment.getStatus())
                .build();
    }

    private ShipmentItemResponse mapShipmentItemResponse(ShipmentItem item) {

        return ShipmentItemResponse.builder()
                .id(item.getId())
                .documentType(item.getDocumentType())
                .documentNumber(item.getDocumentNumber())
                .customerName(item.getCustomerName())
                .policyNumber(item.getPolicyNumber())
                .pageCount(item.getPageCount())
                .received(item.getReceived())
                .build();
    }

}

