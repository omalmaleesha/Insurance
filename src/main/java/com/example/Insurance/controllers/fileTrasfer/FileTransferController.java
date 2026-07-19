package com.example.Insurance.controllers.fileTrasfer;

import com.example.Insurance.dto.delivery.DeliveryConfirmationRequest;
import com.example.Insurance.dto.delivery.DeliveryConfirmationResponse;
import com.example.Insurance.dto.shipment.CreateShipmentRequest;
import com.example.Insurance.dto.shipment.DispatchShipmentRequest;
import com.example.Insurance.dto.shipment.ShipmentResponse;
import com.example.Insurance.dto.shipmentitem.ShipmentItemRequest;
import com.example.Insurance.dto.shipmentitem.ShipmentItemResponse;
import com.example.Insurance.services.FileTransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/file-transfer")
@RequiredArgsConstructor
public class FileTransferController {

    private final FileTransferService service;

    /**
     * Create Shipment
     */
    @PostMapping("/shipments")
    public ShipmentResponse createShipment(
            @RequestBody CreateShipmentRequest request,
            Authentication authentication
    ) {
        return service.createShipment(request, authentication);
    }

    /**
     * Add Document
     */
    @PostMapping("/shipments/{shipmentId}/items")
    public ShipmentItemResponse addItem(
            @PathVariable Long shipmentId,
            @RequestBody ShipmentItemRequest request,
            Authentication authentication
    ) {
        return service.addItem(shipmentId, request, authentication);
    }

    /**
     * Get Shipment Documents
     */
    @GetMapping("/shipments/{shipmentId}/items")
    public List<ShipmentItemResponse> getItems(
            @PathVariable Long shipmentId
    ) {
        return service.getItems(shipmentId);
    }

    /**
     * Dispatch Shipment
     */
    @PutMapping("/shipments/{shipmentId}/dispatch")
    public ShipmentResponse dispatchShipment(
            @PathVariable Long shipmentId,
            @RequestBody DispatchShipmentRequest request,
            Authentication authentication
    ) {
        return service.dispatchShipment(shipmentId, request, authentication);
    }

    /**
     * Incoming Shipments
     */
    @GetMapping("/shipments/incoming")
    public List<ShipmentResponse> incomingShipments(
            Authentication authentication
    ) {
        return service.getIncomingShipments(authentication);
    }

    /**
     * Outgoing Shipments
     */
    @GetMapping("/shipments/outgoing")
    public List<ShipmentResponse> outgoingShipments(
            Authentication authentication
    ) {
        return service.getOutgoingShipments(authentication);
    }

    /**
     * Shipment Details
     */
    @GetMapping("/shipments/{shipmentId}")
    public ShipmentResponse shipmentDetails(
            @PathVariable Long shipmentId
    ) {
        return service.getShipment(shipmentId);
    }

    /**
     * Receive Shipment
     */
    @PutMapping("/shipments/{shipmentId}/receive")
    public DeliveryConfirmationResponse receiveShipment(
            @PathVariable Long shipmentId,
            @RequestBody DeliveryConfirmationRequest request,
            Authentication authentication
    ) {
        return service.receiveShipment(shipmentId, request, authentication);
    }

    /**
     * Shipment History
     */
    @GetMapping("/shipments/history")
    public List<ShipmentResponse> history(
            Authentication authentication
    ) {
        return service.getHistory(authentication);
    }

    /**
     * Cancel Shipment
     */
    @DeleteMapping("/shipments/{shipmentId}")
    public void cancelShipment(
            @PathVariable Long shipmentId,
            Authentication authentication
    ) {
        service.cancelShipment(shipmentId, authentication);
    }
}