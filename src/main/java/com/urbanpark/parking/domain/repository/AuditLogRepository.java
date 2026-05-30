package com.urbanpark.parking.domain.repository;

import com.urbanpark.parking.domain.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByTenantIdOrderByCreatedAtDesc(Long tenantId);
    List<AuditLog> findAllByOrderByCreatedAtDesc();
    List<AuditLog> findByTenantIdAndCreatedAtBetweenOrderByCreatedAtDesc(
            Long tenantId, LocalDateTime from, LocalDateTime to);
}
