package com.example.Insurance.repository;

import com.example.Insurance.entities.CourierShipment;
import com.example.Insurance.entities.ShipmentItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShipmentItemRepository extends JpaRepository<ShipmentItem, Long> {

    List<ShipmentItem> findByShipment(CourierShipment shipment);

    long countByShipment(CourierShipment shipment);

}