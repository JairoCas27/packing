package com.urbanpark.parking.domain.repository;

import com.urbanpark.parking.domain.entity.ParkingUser;
import com.urbanpark.parking.domain.enums.ParkingRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ParkingUserRepository extends JpaRepository<ParkingUser, Long> {
    Optional<ParkingUser> findByTenantIdAndExternalUserId(Long tenantId, String externalUserId);
    Optional<ParkingUser> findByTenantIdAndEmail(Long tenantId, String email);
    List<ParkingUser> findByTenantId(Long tenantId);
    List<ParkingUser> findByTenantIdAndRole(Long tenantId, ParkingRole role);
}
