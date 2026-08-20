package com.example.Insurance.repository.fileTrasfer;

import com.example.Insurance.entities.fileTrasfer.Branch;
import com.example.Insurance.entities.fileTrasfer.CourierShipment;
import com.example.Insurance.utils.types.ShipmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourierShipmentRepository extends JpaRepository<CourierShipment, Long> {

    List<CourierShipment> findByFromBranch(Branch branch);

    List<CourierShipment> findByToBranch(Branch branch);

    List<CourierShipment> findByStatus(ShipmentStatus status);

    List<CourierShipment> findByToBranchAndStatus(Branch branch,
                                                  ShipmentStatus status);

    List<CourierShipment> findByFromBranchAndStatus(Branch branch,
                                                    ShipmentStatus status);



}