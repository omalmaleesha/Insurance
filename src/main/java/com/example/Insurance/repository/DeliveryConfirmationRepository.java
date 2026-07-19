package com.example.Insurance.repository;

import com.example.Insurance.entities.CourierShipment;
import com.example.Insurance.entities.DeliveryConfirmation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeliveryConfirmationRepository
        extends JpaRepository<DeliveryConfirmation, Long> {

    Optional<DeliveryConfirmation> findByShipment(CourierShipment shipment);

    boolean existsByShipment(CourierShipment shipment);

}