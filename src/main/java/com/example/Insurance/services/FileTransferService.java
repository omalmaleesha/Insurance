package com.example.Insurance.services;

import com.example.Insurance.dto.delivery.DeliveryConfirmationRequest;
import com.example.Insurance.dto.delivery.DeliveryConfirmationResponse;
import com.example.Insurance.dto.shipment.CreateShipmentRequest;
import com.example.Insurance.dto.shipment.DispatchShipmentRequest;
import com.example.Insurance.dto.shipment.ShipmentResponse;
import com.example.Insurance.dto.shipmentitem.ShipmentItemRequest;
import com.example.Insurance.dto.shipmentitem.ShipmentItemResponse;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface FileTransferService {

    ShipmentResponse createShipment(CreateShipmentRequest request,
                                    Authentication authentication);

    ShipmentItemResponse addItem(Long shipmentId,
                                 ShipmentItemRequest request,
                                 Authentication authentication);

    List<ShipmentItemResponse> getItems(Long shipmentId);

    ShipmentResponse dispatchShipment(Long shipmentId,
                                      DispatchShipmentRequest request,
                                      Authentication authentication);

    List<ShipmentResponse> getIncomingShipments(Authentication authentication);

    List<ShipmentResponse> getOutgoingShipments(Authentication authentication);

    ShipmentResponse getShipment(Long shipmentId);

    DeliveryConfirmationResponse receiveShipment(Long shipmentId,
                                                 DeliveryConfirmationRequest request,
                                                 Authentication authentication);

    List<ShipmentResponse> getHistory(Authentication authentication);

    void cancelShipment(Long shipmentId,
                        Authentication authentication);
}