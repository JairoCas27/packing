package com.urbanpark.parking.service;

import com.urbanpark.parking.domain.entity.ParkingRule;
import com.urbanpark.parking.domain.entity.Tenant;
import com.urbanpark.parking.domain.repository.ParkingRuleRepository;
import com.urbanpark.parking.dto.request.UpdateParkingRuleRequest;
import com.urbanpark.parking.dto.response.ParkingRuleResponse;
import com.urbanpark.parking.exception.ApiException;
import com.urbanpark.parking.mapper.ResponseMapper;
import com.urbanpark.parking.security.SecurityUtils;
import com.urbanpark.parking.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ParkingRuleService {

    private final ParkingRuleRepository parkingRuleRepository;
    private final TenantService tenantService;
    private final ResponseMapper responseMapper;
    private final SecurityUtils securityUtils;
    private final AuditService auditService;

    @Transactional(readOnly = true)
    public ParkingRuleResponse getRules() {
        Long tenantId = securityUtils.requireTenantId();
        return responseMapper.toParkingRuleResponse(getOrCreateRules(tenantId));
    }

    @Transactional
    public ParkingRuleResponse updateRules(UpdateParkingRuleRequest request) {
        securityUtils.requireParkingRole("ADMIN_CONDOMINIO");
        Long tenantId = securityUtils.requireTenantId();
        ParkingRule rules = getOrCreateRules(tenantId);

        if (request.getMaxVehiclesPerUser() != null) rules.setMaxVehiclesPerUser(request.getMaxVehiclesPerUser());
        if (request.getMaxVisitorsPerDay() != null) rules.setMaxVisitorsPerDay(request.getMaxVisitorsPerDay());
        if (request.getAccessStartTime() != null) rules.setAccessStartTime(request.getAccessStartTime());
        if (request.getAccessEndTime() != null) rules.setAccessEndTime(request.getAccessEndTime());
        if (request.getAllowVisitorsOutsideHours() != null) {
            rules.setAllowVisitorsOutsideHours(request.getAllowVisitorsOutsideHours());
        }
        if (request.getMaxAutos() != null) rules.setMaxAutos(request.getMaxAutos());
        if (request.getMaxMotos() != null) rules.setMaxMotos(request.getMaxMotos());

        rules = parkingRuleRepository.save(rules);

        UserPrincipal actor = securityUtils.getCurrentUser();
        auditService.log(actor, tenantId, "PARKING_RULES_UPDATED", "ParkingRule",
                rules.getId().toString(), null);

        return responseMapper.toParkingRuleResponse(rules);
    }

    public ParkingRule getOrCreateRules(Long tenantId) {
        return parkingRuleRepository.findByTenantId(tenantId)
                .orElseGet(() -> {
                    Tenant tenant = tenantService.findTenant(tenantId);
                    return parkingRuleRepository.save(ParkingRule.builder().tenant(tenant).build());
                });
    }

    public void validateAccessRules(Long tenantId, boolean isVisitor, java.time.LocalTime now) {
        ParkingRule rules = getOrCreateRules(tenantId);
        if (rules.getAccessStartTime() != null && rules.getAccessEndTime() != null) {
            boolean withinHours = !now.isBefore(rules.getAccessStartTime()) && !now.isAfter(rules.getAccessEndTime());
            if (!withinHours && !(isVisitor && rules.isAllowVisitorsOutsideHours())) {
                throw new ApiException("Acceso fuera del horario permitido", HttpStatus.FORBIDDEN);
            }
        }
    }
}
