package com.urbanpark.parking.domain.repository;

import com.urbanpark.parking.domain.entity.SecurityIncident;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SecurityIncidentRepository extends JpaRepository<SecurityIncident, Long> {
    List<SecurityIncident> findByTenantIdOrderByCreatedAtDesc(Long tenantId);
}
