package com.example.Insurance.repository.fileTrasfer;

import com.example.Insurance.entities.fileTrasfer.CourierShipment;
import com.example.Insurance.entities.fileTrasfer.DeliveryConfirmation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeliveryConfirmationRepository
        extends JpaRepository<DeliveryConfirmation, Long> {

    Optional<DeliveryConfirmation> findByShipment(CourierShipment shipment);

    boolean existsByShipment(CourierShipment shipment);

}