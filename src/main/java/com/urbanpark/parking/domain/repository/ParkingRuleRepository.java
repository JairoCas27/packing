package com.urbanpark.parking.domain.repository;

import com.urbanpark.parking.domain.entity.ParkingRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParkingRuleRepository extends JpaRepository<ParkingRule, Long> {
    Optional<ParkingRule> findByTenantId(Long tenantId);
}
