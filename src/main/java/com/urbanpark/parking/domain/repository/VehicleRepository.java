package com.urbanpark.parking.domain.repository;

import com.urbanpark.parking.domain.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    List<Vehicle> findByTenantId(Long tenantId);
    List<Vehicle> findByTenantIdAndOwnerId(Long tenantId, Long ownerId);
    Optional<Vehicle> findByTenantIdAndPlacaIgnoreCase(Long tenantId, String placa);
    Optional<Vehicle> findByTenantIdAndExternalVehicleId(Long tenantId, Long externalVehicleId);
    long countByTenantIdAndOwnerIdAndActiveTrue(Long tenantId, Long ownerId);
}
