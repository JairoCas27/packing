package com.urbanpark.parking.service;

import com.urbanpark.parking.domain.repository.AuditLogRepository;
import com.urbanpark.parking.dto.response.AuditLogResponse;
import com.urbanpark.parking.mapper.ResponseMapper;
import com.urbanpark.parking.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditQueryService {

    private final AuditLogRepository auditLogRepository;
    private final ResponseMapper responseMapper;
    private final SecurityUtils securityUtils;

    @Transactional(readOnly = true)
    public List<AuditLogResponse> listByTenant(Long tenantId, LocalDateTime from, LocalDateTime to) {
        var user = securityUtils.getCurrentUser();
        if (user.isSaasUser()) {
            securityUtils.requireSaasRole("SUPERADMIN", "ADMIN");
        } else if (!tenantId.equals(user.getTenantId())) {
            securityUtils.requireParkingRole("ADMIN_CONDOMINIO");
        } else {
            securityUtils.requireParkingRole("ADMIN_CONDOMINIO");
        }

        if (from != null && to != null) {
            return auditLogRepository.findByTenantIdAndCreatedAtBetweenOrderByCreatedAtDesc(tenantId, from, to)
                    .stream().map(responseMapper::toAuditLogResponse).toList();
        }
        return auditLogRepository.findByTenantIdOrderByCreatedAtDesc(tenantId).stream()
                .map(responseMapper::toAuditLogResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<AuditLogResponse> listGlobal() {
        securityUtils.requireSaasRole("SUPERADMIN", "ADMIN");
        return auditLogRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(responseMapper::toAuditLogResponse).toList();
    }
}
