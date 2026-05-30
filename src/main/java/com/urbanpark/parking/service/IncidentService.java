package com.urbanpark.parking.service;

import com.urbanpark.parking.domain.entity.AccessEvent;
import com.urbanpark.parking.domain.entity.ParkingUser;
import com.urbanpark.parking.domain.entity.SecurityIncident;
import com.urbanpark.parking.domain.entity.Tenant;
import com.urbanpark.parking.domain.repository.AccessEventRepository;
import com.urbanpark.parking.domain.repository.ParkingUserRepository;
import com.urbanpark.parking.domain.repository.SecurityIncidentRepository;
import com.urbanpark.parking.dto.request.CreateIncidentRequest;
import com.urbanpark.parking.exception.ApiException;
import com.urbanpark.parking.security.SecurityUtils;
import com.urbanpark.parking.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IncidentService {

    private final SecurityIncidentRepository incidentRepository;
    private final AccessEventRepository accessEventRepository;
    private final ParkingUserRepository parkingUserRepository;
    private final TenantService tenantService;
    private final SecurityUtils securityUtils;
    private final AuditService auditService;

    @Transactional
    public SecurityIncident create(CreateIncidentRequest request) {
        securityUtils.requireParkingRole("AGENTE_SEGURIDAD", "ADMIN_CONDOMINIO");
        Long tenantId = securityUtils.requireTenantId();
        UserPrincipal current = securityUtils.getCurrentUser();
        Tenant tenant = tenantService.findTenant(tenantId);

        ParkingUser reporter = parkingUserRepository.findById(Long.parseLong(current.getUserId()))
                .orElseThrow(() -> new ApiException("Usuario no encontrado", HttpStatus.NOT_FOUND));

        AccessEvent accessEvent = null;
        if (request.getAccessEventId() != null) {
            accessEvent = accessEventRepository.findById(request.getAccessEventId())
                    .filter(e -> e.getTenant().getId().equals(tenantId))
                    .orElseThrow(() -> new ApiException("Evento de acceso no encontrado", HttpStatus.NOT_FOUND));
        }

        SecurityIncident incident = SecurityIncident.builder()
                .tenant(tenant)
                .description(request.getDescription())
                .type(request.getType())
                .accessEvent(accessEvent)
                .reportedBy(reporter)
                .build();

        incident = incidentRepository.save(incident);
        auditService.log(current, tenantId, "INCIDENT_CREATED", "SecurityIncident",
                incident.getId().toString(), request.getDescription());
        return incident;
    }

    @Transactional(readOnly = true)
    public List<SecurityIncident> list() {
        Long tenantId = securityUtils.requireTenantId();
        securityUtils.requireParkingRole("AGENTE_SEGURIDAD", "ADMIN_CONDOMINIO");
        return incidentRepository.findByTenantIdOrderByCreatedAtDesc(tenantId);
    }
}
