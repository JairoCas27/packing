package com.urbanpark.parking.service;

import com.urbanpark.parking.domain.entity.ParkingUser;
import com.urbanpark.parking.domain.entity.Tenant;
import com.urbanpark.parking.domain.entity.Vehicle;
import com.urbanpark.parking.domain.repository.ParkingUserRepository;
import com.urbanpark.parking.domain.repository.VehicleRepository;
import com.urbanpark.parking.dto.request.CreateVehicleRequest;
import com.urbanpark.parking.dto.response.VehicleResponse;
import com.urbanpark.parking.exception.ApiException;
import com.urbanpark.parking.mapper.ResponseMapper;
import com.urbanpark.parking.security.SecurityUtils;
import com.urbanpark.parking.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final ParkingUserRepository parkingUserRepository;
    private final ParkingRuleService parkingRuleService;
    private final TenantService tenantService;
    private final ResponseMapper responseMapper;
    private final SecurityUtils securityUtils;
    private final AuditService auditService;

    @Transactional
    public VehicleResponse create(CreateVehicleRequest request) {
        Long tenantId = securityUtils.requireTenantId();
        UserPrincipal current = securityUtils.getCurrentUser();
        Tenant tenant = tenantService.findTenant(tenantId);

        ParkingUser owner = resolveOwner(current, tenantId);

        var rules = parkingRuleService.getOrCreateRules(tenantId);
        long currentCount = vehicleRepository.countByTenantIdAndOwnerIdAndActiveTrue(tenantId, owner.getId());
        if (currentCount >= rules.getMaxVehiclesPerUser()) {
            throw new ApiException("Límite de vehículos alcanzado", HttpStatus.BAD_REQUEST);
        }

        vehicleRepository.findByTenantIdAndPlacaIgnoreCase(tenantId, request.getPlaca())
                .ifPresent(v -> {
                    throw new ApiException("La placa ya está registrada", HttpStatus.CONFLICT);
                });

        Vehicle vehicle = Vehicle.builder()
                .tenant(tenant)
                .placa(request.getPlaca().toUpperCase())
                .marca(request.getMarca())
                .modelo(request.getModelo())
                .color(request.getColor())
                .tipo(request.getTipo())
                .owner(owner)
                .active(true)
                .build();

        vehicle = vehicleRepository.save(vehicle);
        auditService.log(current, tenantId, "VEHICLE_CREATED", "Vehicle", vehicle.getId().toString(), vehicle.getPlaca());
        return responseMapper.toVehicleResponse(vehicle);
    }

    @Transactional(readOnly = true)
    public List<VehicleResponse> list() {
        Long tenantId = securityUtils.requireTenantId();
        UserPrincipal current = securityUtils.getCurrentUser();

        if ("PROPIETARIO".equals(current.getRole())) {
            Long ownerId = Long.parseLong(current.getUserId());
            return vehicleRepository.findByTenantIdAndOwnerId(tenantId, ownerId).stream()
                    .map(responseMapper::toVehicleResponse)
                    .toList();
        }

        return vehicleRepository.findByTenantId(tenantId).stream()
                .map(responseMapper::toVehicleResponse)
                .toList();
    }

    @Transactional
    public void delete(Long id) {
        Long tenantId = securityUtils.requireTenantId();
        UserPrincipal current = securityUtils.getCurrentUser();
        Vehicle vehicle = findByTenant(tenantId, id);

        if ("PROPIETARIO".equals(current.getRole()) &&
                !vehicle.getOwner().getId().equals(Long.parseLong(current.getUserId()))) {
            throw new ApiException("No puede eliminar vehículos de otro usuario", HttpStatus.FORBIDDEN);
        }

        vehicle.setActive(false);
        vehicleRepository.save(vehicle);
        auditService.log(current, tenantId, "VEHICLE_DELETED", "Vehicle", id.toString(), null);
    }

    public Vehicle findByPlaca(Long tenantId, String placa) {
        return vehicleRepository.findByTenantIdAndPlacaIgnoreCase(tenantId, placa)
                .filter(Vehicle::isActive)
                .orElse(null);
    }

    public Vehicle findByTenant(Long tenantId, Long id) {
        return vehicleRepository.findById(id)
                .filter(v -> v.getTenant().getId().equals(tenantId) && v.isActive())
                .orElseThrow(() -> new ApiException("Vehículo no encontrado", HttpStatus.NOT_FOUND));
    }

    private ParkingUser resolveOwner(UserPrincipal current, Long tenantId) {
        if ("PROPIETARIO".equals(current.getRole())) {
            return parkingUserRepository.findById(Long.parseLong(current.getUserId()))
                    .orElseThrow(() -> new ApiException("Usuario no encontrado", HttpStatus.NOT_FOUND));
        }
        securityUtils.requireParkingRole("ADMIN_CONDOMINIO");
        throw new ApiException("Debe especificar propietario", HttpStatus.BAD_REQUEST);
    }
}
