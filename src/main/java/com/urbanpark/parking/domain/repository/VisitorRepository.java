package com.urbanpark.parking.domain.repository;

import com.urbanpark.parking.domain.entity.Visitor;
import com.urbanpark.parking.domain.enums.VisitorStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VisitorRepository extends JpaRepository<Visitor, Long> {
    List<Visitor> findByTenantIdAndHostUserId(Long tenantId, Long hostUserId);
    Optional<Visitor> findByTenantIdAndPlacaIgnoreCaseAndStatus(Long tenantId, String placa, VisitorStatus status);
    List<Visitor> findByTenantIdAndStatusAndValidUntilBefore(Long tenantId, VisitorStatus status, LocalDateTime before);
    long countByTenantIdAndHostUserIdAndCreatedAtAfter(Long tenantId, Long hostUserId, LocalDateTime after);
}
