package com.urbanpark.parking.service;

import com.urbanpark.parking.domain.entity.ParkingUser;
import com.urbanpark.parking.domain.entity.Tenant;
import com.urbanpark.parking.domain.entity.Visitor;
import com.urbanpark.parking.domain.enums.VisitorStatus;
import com.urbanpark.parking.domain.repository.ParkingUserRepository;
import com.urbanpark.parking.domain.repository.VisitorRepository;
import com.urbanpark.parking.dto.request.CreateVisitorRequest;
import com.urbanpark.parking.dto.response.VisitorResponse;
import com.urbanpark.parking.exception.ApiException;
import com.urbanpark.parking.mapper.ResponseMapper;
import com.urbanpark.parking.security.SecurityUtils;
import com.urbanpark.parking.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VisitorService {

    private final VisitorRepository visitorRepository;
    private final ParkingUserRepository parkingUserRepository;
    private final ParkingRuleService parkingRuleService;
    private final TenantService tenantService;
    private final ResponseMapper responseMapper;
    private final SecurityUtils securityUtils;
    private final AuditService auditService;

    @Transactional
    public VisitorResponse create(CreateVisitorRequest request) {
        securityUtils.requireParkingRole("PROPIETARIO");
        Long tenantId = securityUtils.requireTenantId();
        UserPrincipal current = securityUtils.getCurrentUser();
        Tenant tenant = tenantService.findTenant(tenantId);

        ParkingUser host = parkingUserRepository.findById(Long.parseLong(current.getUserId()))
                .orElseThrow(() -> new ApiException("Usuario no encontrado", HttpStatus.NOT_FOUND));

        if (request.getValidUntil().isBefore(request.getValidFrom())) {
            throw new ApiException("La fecha de fin debe ser posterior al inicio", HttpStatus.BAD_REQUEST);
        }

        var rules = parkingRuleService.getOrCreateRules(tenantId);
        LocalDateTime startOfDay = LocalDateTime.now().with(LocalTime.MIN);
        long todayCount = visitorRepository.countByTenantIdAndHostUserIdAndCreatedAtAfter(
                tenantId, host.getId(), startOfDay);
        if (todayCount >= rules.getMaxVisitorsPerDay()) {
            throw new ApiException("Límite diario de visitantes alcanzado", HttpStatus.BAD_REQUEST);
        }

        Visitor visitor = Visitor.builder()
                .tenant(tenant)
                .placa(request.getPlaca().toUpperCase())
                .nombre(request.getNombre())
                .documento(request.getDocumento())
                .hostUser(host)
                .validFrom(request.getValidFrom())
                .validUntil(request.getValidUntil())
                .status(VisitorStatus.ACTIVE)
                .build();

        visitor = visitorRepository.save(visitor);
        auditService.log(current, tenantId, "VISITOR_CREATED", "Visitor", visitor.getId().toString(), visitor.getPlaca());
        return responseMapper.toVisitorResponse(visitor);
    }

    @Transactional(readOnly = true)
    public List<VisitorResponse> list() {
        Long tenantId = securityUtils.requireTenantId();
        UserPrincipal current = securityUtils.getCurrentUser();

        if ("PROPIETARIO".equals(current.getRole())) {
            return visitorRepository.findByTenantIdAndHostUserId(tenantId, Long.parseLong(current.getUserId()))
                    .stream().map(responseMapper::toVisitorResponse).toList();
        }

        securityUtils.requireParkingRole("ADMIN_CONDOMINIO", "AGENTE_SEGURIDAD");
        return visitorRepository.findAll().stream()
                .filter(v -> v.getTenant().getId().equals(tenantId))
                .map(responseMapper::toVisitorResponse)
                .toList();
    }

    public Visitor findActiveVisitor(Long tenantId, String placa) {
        return visitorRepository.findByTenantIdAndPlacaIgnoreCaseAndStatus(tenantId, placa, VisitorStatus.ACTIVE)
                .filter(v -> {
                    LocalDateTime now = LocalDateTime.now();
                    return !now.isBefore(v.getValidFrom()) && !now.isAfter(v.getValidUntil());
                })
                .orElse(null);
    }
}
