package com.urbanpark.parking.domain.repository;

import com.urbanpark.parking.domain.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TenantRepository extends JpaRepository<Tenant, Long> {
    Optional<Tenant> findByExternalCondominioId(Long externalCondominioId);
    boolean existsByExternalCondominioId(Long externalCondominioId);
}
