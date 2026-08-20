package com.example.Insurance.repository.fileTrasfer;

import com.example.Insurance.entities.fileTrasfer.CourierShipment;
import com.example.Insurance.entities.fileTrasfer.ShipmentItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShipmentItemRepository extends JpaRepository<ShipmentItem, Long> {

    List<ShipmentItem> findByShipment(CourierShipment shipment);

    long countByShipment(CourierShipment shipment);

}