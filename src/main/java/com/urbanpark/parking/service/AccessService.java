package com.urbanpark.parking.service;

import com.urbanpark.parking.domain.entity.*;
import com.urbanpark.parking.domain.enums.*;
import com.urbanpark.parking.domain.repository.AccessEventRepository;
import com.urbanpark.parking.domain.repository.ParkingUserRepository;
import com.urbanpark.parking.dto.request.RegisterAccessRequest;
import com.urbanpark.parking.dto.response.AccessEventResponse;
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
public class AccessService {

    private final AccessEventRepository accessEventRepository;
    private final VehicleService vehicleService;
    private final VisitorService visitorService;
    private final ParkingSpaceService parkingSpaceService;
    private final ParkingRuleService parkingRuleService;
    private final NotificationService notificationService;
    private final TenantService tenantService;
    private final ParkingUserRepository parkingUserRepository;
    private final ResponseMapper responseMapper;
    private final SecurityUtils securityUtils;
    private final AuditService auditService;

    @Transactional
    public AccessEventResponse registerEntry(RegisterAccessRequest request) {
        securityUtils.requireParkingRole("AGENTE_SEGURIDAD", "ADMIN_CONDOMINIO");
        return processAccess(request, AccessType.ENTRY);
    }

    @Transactional
    public AccessEventResponse registerExit(RegisterAccessRequest request) {
        securityUtils.requireParkingRole("AGENTE_SEGURIDAD", "ADMIN_CONDOMINIO");
        return processAccess(request, AccessType.EXIT);
    }

    @Transactional(readOnly = true)
    public List<AccessEventResponse> listEvents() {
        Long tenantId = securityUtils.requireTenantId();
        UserPrincipal current = securityUtils.getCurrentUser();

        if ("PROPIETARIO".equals(current.getRole())) {
            return accessEventRepository
                    .findByTenantIdAndParkingUserIdOrderByCreatedAtDesc(tenantId, Long.parseLong(current.getUserId()))
                    .stream().map(responseMapper::toAccessEventResponse).toList();
        }

        return accessEventRepository.findByTenantIdOrderByCreatedAtDesc(tenantId).stream()
                .map(responseMapper::toAccessEventResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AccessEventResponse validatePlate(String placa) {
        securityUtils.requireParkingRole("AGENTE_SEGURIDAD", "ADMIN_CONDOMINIO");
        Long tenantId = securityUtils.requireTenantId();
        String normalizedPlaca = placa.toUpperCase();

        Vehicle vehicle = vehicleService.findByPlaca(tenantId, normalizedPlaca);
        Visitor visitor = visitorService.findActiveVisitor(tenantId, normalizedPlaca);

        AccessStatus status = (vehicle != null || visitor != null) ? AccessStatus.AUTHORIZED : AccessStatus.DENIED;

        AccessEvent preview = AccessEvent.builder()
                .placa(normalizedPlaca)
                .type(AccessType.ENTRY)
                .method(AccessMethod.MANUAL)
                .status(status)
                .vehicle(vehicle)
                .visitor(visitor)
                .parkingUser(vehicle != null ? vehicle.getOwner() : (visitor != null ? visitor.getHostUser() : null))
                .denialReason(status == AccessStatus.DENIED ? "Placa no autorizada" : null)
                .build();

        return responseMapper.toAccessEventResponse(preview);
    }

    private AccessEventResponse processAccess(RegisterAccessRequest request, AccessType type) {
        Long tenantId = securityUtils.requireTenantId();
        UserPrincipal current = securityUtils.getCurrentUser();
        Tenant tenant = tenantService.findTenant(tenantId);
        String placa = request.getPlaca().toUpperCase();
        LocalDateTime now = LocalDateTime.now();

        ParkingUser agent = parkingUserRepository.findById(Long.parseLong(current.getUserId())).orElse(null);

        if (type == AccessType.ENTRY) {
            return responseMapper.toAccessEventResponse(
                    handleEntry(request, tenant, placa, now, agent, current));
        }
        return responseMapper.toAccessEventResponse(
                handleExit(request, tenant, placa, now, agent, current));
    }

    private AccessEvent handleEntry(RegisterAccessRequest request, Tenant tenant, String placa,
                                    LocalDateTime now, ParkingUser agent, UserPrincipal current) {
        Long tenantId = tenant.getId();
        Vehicle vehicle = vehicleService.findByPlaca(tenantId, placa);
        Visitor visitor = visitorService.findActiveVisitor(tenantId, placa);
        boolean isVisitor = visitor != null && vehicle == null;

        AccessStatus status = AccessStatus.DENIED;
        String denialReason = "Placa no autorizada";
        ParkingUser parkingUser = null;
        ParkingSpace space = null;

        if (vehicle != null || visitor != null) {
            try {
                if (!request.isForceAllow()) {
                    parkingRuleService.validateAccessRules(tenantId, isVisitor, LocalTime.now());
                }
                status = AccessStatus.AUTHORIZED;
                denialReason = null;
                parkingUser = vehicle != null ? vehicle.getOwner() : visitor.getHostUser();

                if (vehicle != null) {
                    space = vehicle.getParkingSpace();
                    if (space == null) {
                        space = parkingSpaceService.findAvailableSpace(tenantId,
                                vehicle.getTipo() == VehicleType.MOTO ? SpaceType.MOTO : SpaceType.AUTO);
                        if (space != null) {
                            space.setStatus(SpaceStatus.OCCUPIED);
                            vehicle.setParkingSpace(space);
                        }
                    } else {
                        space.setStatus(SpaceStatus.OCCUPIED);
                    }
                } else {
                    space = parkingSpaceService.findAvailableSpace(tenantId, SpaceType.VISITANTE);
                    if (space != null) {
                        space.setStatus(SpaceStatus.OCCUPIED);
                    }
                }
            } catch (ApiException ex) {
                denialReason = ex.getMessage();
            }
        }

        AccessEvent event = AccessEvent.builder()
                .tenant(tenant)
                .placa(placa)
                .type(AccessType.ENTRY)
                .method(request.getMethod() != null ? request.getMethod() : AccessMethod.MANUAL)
                .status(status)
                .vehicle(vehicle)
                .visitor(visitor)
                .parkingUser(parkingUser)
                .parkingSpace(space)
                .registeredBy(agent)
                .denialReason(denialReason)
                .entryTimestamp(now)
                .build();

        event = accessEventRepository.save(event);

        if (status == AccessStatus.AUTHORIZED && parkingUser != null) {
            notificationService.notifyAccess(parkingUser, event);
        } else if (status == AccessStatus.DENIED) {
            notificationService.notifyUnauthorized(tenant, placa);
        }

        auditService.log(current, tenantId,
                status == AccessStatus.AUTHORIZED ? "ACCESS_ENTRY" : "ACCESS_DENIED",
                "AccessEvent", event.getId().toString(), placa);

        return event;
    }

    private AccessEvent handleExit(RegisterAccessRequest request, Tenant tenant, String placa,
                                   LocalDateTime now, ParkingUser agent, UserPrincipal current) {
        Long tenantId = tenant.getId();

        AccessEvent openEntry = accessEventRepository.findOpenEntry(tenantId, placa)
                .orElse(null);

        AccessEvent event = AccessEvent.builder()
                .tenant(tenant)
                .placa(placa)
                .type(AccessType.EXIT)
                .method(request.getMethod() != null ? request.getMethod() : AccessMethod.MANUAL)
                .status(openEntry != null ? AccessStatus.AUTHORIZED : AccessStatus.DENIED)
                .vehicle(openEntry != null ? openEntry.getVehicle() : vehicleService.findByPlaca(tenantId, placa))
                .visitor(openEntry != null ? openEntry.getVisitor() : null)
                .parkingUser(openEntry != null ? openEntry.getParkingUser() : null)
                .parkingSpace(openEntry != null ? openEntry.getParkingSpace() : null)
                .registeredBy(agent)
                .denialReason(openEntry == null ? "No hay entrada abierta para esta placa" : null)
                .exitTimestamp(now)
                .linkedEntry(openEntry)
                .build();

        if (openEntry != null) {
            openEntry.setExitTimestamp(now);
            accessEventRepository.save(openEntry);

            if (openEntry.getParkingSpace() != null) {
                openEntry.getParkingSpace().setStatus(SpaceStatus.AVAILABLE);
            }
            if (openEntry.getVehicle() != null) {
                openEntry.getVehicle().setParkingSpace(null);
            }
        }

        event = accessEventRepository.save(event);

        if (openEntry != null && openEntry.getParkingUser() != null) {
            notificationService.notifyExit(openEntry.getParkingUser(), event);
        }

        auditService.log(current, tenantId, "ACCESS_EXIT", "AccessEvent", event.getId().toString(), placa);
        return event;
    }
}
