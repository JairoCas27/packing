package com.urbanpark.parking.mapper;

import com.urbanpark.parking.domain.entity.*;
import com.urbanpark.parking.dto.response.*;
import org.springframework.stereotype.Component;

@Component
public class ResponseMapper {

    public TenantResponse toTenantResponse(Tenant tenant) {
        return TenantResponse.builder()
                .id(tenant.getId())
                .name(tenant.getName())
                .externalCondominioId(tenant.getExternalCondominioId())
                .apiBaseUrl(tenant.getApiBaseUrl())
                .active(tenant.isActive())
                .country(tenant.getCountry())
                .city(tenant.getCity())
                .address(tenant.getAddress())
                .createdAt(tenant.getCreatedAt())
                .build();
    }

    public ParkingSpaceResponse toParkingSpaceResponse(ParkingSpace space) {
        return ParkingSpaceResponse.builder()
                .id(space.getId())
                .code(space.getCode())
                .zone(space.getZone())
                .type(space.getType())
                .status(space.getStatus())
                .assignedUserId(space.getAssignedUser() != null ? space.getAssignedUser().getId() : null)
                .build();
    }

    public VehicleResponse toVehicleResponse(Vehicle vehicle) {
        return VehicleResponse.builder()
                .id(vehicle.getId())
                .placa(vehicle.getPlaca())
                .marca(vehicle.getMarca())
                .modelo(vehicle.getModelo())
                .color(vehicle.getColor())
                .tipo(vehicle.getTipo())
                .ownerId(vehicle.getOwner() != null ? vehicle.getOwner().getId() : null)
                .parkingSpaceId(vehicle.getParkingSpace() != null ? vehicle.getParkingSpace().getId() : null)
                .active(vehicle.isActive())
                .build();
    }

    public VisitorResponse toVisitorResponse(Visitor visitor) {
        return VisitorResponse.builder()
                .id(visitor.getId())
                .placa(visitor.getPlaca())
                .nombre(visitor.getNombre())
                .documento(visitor.getDocumento())
                .hostUserId(visitor.getHostUser().getId())
                .validFrom(visitor.getValidFrom())
                .validUntil(visitor.getValidUntil())
                .status(visitor.getStatus())
                .build();
    }

    public AccessEventResponse toAccessEventResponse(AccessEvent event) {
        return AccessEventResponse.builder()
                .id(event.getId())
                .placa(event.getPlaca())
                .type(event.getType())
                .method(event.getMethod())
                .status(event.getStatus())
                .vehicleId(event.getVehicle() != null ? event.getVehicle().getId() : null)
                .visitorId(event.getVisitor() != null ? event.getVisitor().getId() : null)
                .parkingUserId(event.getParkingUser() != null ? event.getParkingUser().getId() : null)
                .parkingSpaceId(event.getParkingSpace() != null ? event.getParkingSpace().getId() : null)
                .denialReason(event.getDenialReason())
                .entryTimestamp(event.getEntryTimestamp())
                .exitTimestamp(event.getExitTimestamp())
                .createdAt(event.getCreatedAt())
                .build();
    }

    public ParkingRuleResponse toParkingRuleResponse(ParkingRule rule) {
        return ParkingRuleResponse.builder()
                .id(rule.getId())
                .maxVehiclesPerUser(rule.getMaxVehiclesPerUser())
                .maxVisitorsPerDay(rule.getMaxVisitorsPerDay())
                .accessStartTime(rule.getAccessStartTime())
                .accessEndTime(rule.getAccessEndTime())
                .allowVisitorsOutsideHours(rule.isAllowVisitorsOutsideHours())
                .maxAutos(rule.getMaxAutos())
                .maxMotos(rule.getMaxMotos())
                .build();
    }

    public AuditLogResponse toAuditLogResponse(AuditLog log) {
        return AuditLogResponse.builder()
                .id(log.getId())
                .tenantId(log.getTenantId())
                .actorId(log.getActorId())
                .actorRole(log.getActorRole())
                .action(log.getAction())
                .entityType(log.getEntityType())
                .entityId(log.getEntityId())
                .details(log.getDetails())
                .createdAt(log.getCreatedAt())
                .build();
    }

    public NotificationResponse toNotificationResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .type(notification.getType())
                .read(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
