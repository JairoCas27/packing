package com.urbanpark.parking.domain.repository;

import com.urbanpark.parking.domain.entity.ParkingSpace;
import com.urbanpark.parking.domain.enums.SpaceStatus;
import com.urbanpark.parking.domain.enums.SpaceType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ParkingSpaceRepository extends JpaRepository<ParkingSpace, Long> {
    List<ParkingSpace> findByTenantId(Long tenantId);
    Optional<ParkingSpace> findByTenantIdAndCode(Long tenantId, String code);
    List<ParkingSpace> findByTenantIdAndStatus(Long tenantId, SpaceStatus status);
    List<ParkingSpace> findByTenantIdAndTypeAndStatus(Long tenantId, SpaceType type, SpaceStatus status);
    long countByTenantIdAndStatus(Long tenantId, SpaceStatus status);
}
