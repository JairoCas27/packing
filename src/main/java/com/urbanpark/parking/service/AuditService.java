package com.urbanpark.parking.service;

import com.urbanpark.parking.domain.entity.AuditLog;
import com.urbanpark.parking.domain.repository.AuditLogRepository;
import com.urbanpark.parking.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    @Transactional
    public void log(UserPrincipal actor, Long tenantId, String action,
                    String entityType, String entityId, String details) {
        AuditLog log = AuditLog.builder()
                .tenantId(tenantId)
                .actorId(actor.getUserId())
                .actorRole(actor.getRole())
                .action(action)
                .entityType(entityType)
                .entityId(entityId)
                .details(details)
                .build();
        auditLogRepository.save(log);
    }

    @Transactional
    public void logSystem(Long tenantId, String action, String entityType, String entityId, String details) {
        AuditLog log = AuditLog.builder()
                .tenantId(tenantId)
                .actorId("SYSTEM")
                .actorRole("SYSTEM")
                .action(action)
                .entityType(entityType)
                .entityId(entityId)
                .details(details)
                .build();
        auditLogRepository.save(log);
    }
}
