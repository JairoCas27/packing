package com.urbanpark.parking.service;

import com.urbanpark.parking.domain.entity.Tenant;
import com.urbanpark.parking.domain.entity.Vehicle;
import com.urbanpark.parking.domain.enums.AccessStatus;
import com.urbanpark.parking.domain.enums.AccessType;
import com.urbanpark.parking.domain.enums.VehicleType;
import com.urbanpark.parking.domain.repository.AccessEventRepository;
import com.urbanpark.parking.domain.repository.TenantRepository;
import com.urbanpark.parking.domain.repository.VehicleRepository;
import com.urbanpark.parking.integration.core.CoreApiClient;
import com.urbanpark.parking.integration.core.dto.CorePaginacionResponse;
import com.urbanpark.parking.integration.core.dto.CoreUsuarioResponse;
import com.urbanpark.parking.integration.core.dto.CoreVehiculoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CondominioSyncService {

    private final TenantRepository tenantRepository;
    private final VehicleRepository vehicleRepository;
    private final CoreApiClient coreApiClient;
    private final ParkingUserSyncService parkingUserSyncService;
    private final AuditService auditService;

    @Scheduled(fixedRate = 3600000)
    @Transactional
    public void syncAllTenants() {
        List<Tenant> tenants = tenantRepository.findAll().stream().filter(Tenant::isActive).toList();
        for (Tenant tenant : tenants) {
            try {
                syncTenant(tenant.getId());
            } catch (Exception ex) {
                log.warn("Error sincronizando tenant {}: {}", tenant.getId(), ex.getMessage());
            }
        }
    }

    @Transactional
    public void syncTenant(Long tenantId) {
        Tenant tenant = tenantRepository.findById(tenantId).orElseThrow();
        if (tenant.getApiKey() == null || tenant.getApiKey().isBlank()) {
            log.debug("Tenant {} sin API key, omitiendo sync automático", tenantId);
            return;
        }

        String token = tenant.getApiKey();
        syncUsers(tenant, token);
        syncVehicles(tenant, token);
        auditService.logSystem(tenantId, "SYNC_COMPLETED", "Tenant", tenantId.toString(), null);
    }

    private void syncUsers(Tenant tenant, String token) {
        int page = 0;
        while (true) {
            CorePaginacionResponse<CoreUsuarioResponse> response =
                    coreApiClient.listUsuarios(tenant.getApiBaseUrl(), token, page, 50);
            if (response.getContenido() == null || response.getContenido().isEmpty()) {
                break;
            }
            for (CoreUsuarioResponse user : response.getContenido()) {
                if (user.getCondominioId() == null ||
                        user.getCondominioId().equals(tenant.getExternalCondominioId())) {
                    try {
                        parkingUserSyncService.syncUser(tenant, user);
                    } catch (Exception ex) {
                        log.debug("Usuario {} omitido: {}", user.getId(), ex.getMessage());
                    }
                }
            }
            page++;
            if (response.getTotalPaginas() != null && page >= response.getTotalPaginas()) {
                break;
            }
        }
    }

    private void syncVehicles(Tenant tenant, String token) {
        List<CoreVehiculoResponse> vehiculos = coreApiClient.listVehiculos(tenant.getApiBaseUrl(), token);
        for (CoreVehiculoResponse external : vehiculos) {
            vehicleRepository.findByTenantIdAndExternalVehicleId(tenant.getId(), external.getId())
                    .or(() -> vehicleRepository.findByTenantIdAndPlacaIgnoreCase(tenant.getId(), external.getPlaca()))
                    .ifPresentOrElse(existing -> updateVehicle(existing, external),
                            () -> createVehicle(tenant, external));
        }
    }

    private void updateVehicle(Vehicle vehicle, CoreVehiculoResponse external) {
        vehicle.setMarca(external.getMarca());
        vehicle.setModelo(external.getModelo());
        vehicle.setColor(external.getColor());
        vehicle.setExternalVehicleId(external.getId());
        vehicleRepository.save(vehicle);
    }

    private void createVehicle(Tenant tenant, CoreVehiculoResponse external) {
        if (external.getPlaca() == null) return;
        Vehicle vehicle = Vehicle.builder()
                .tenant(tenant)
                .externalVehicleId(external.getId())
                .placa(external.getPlaca().toUpperCase())
                .marca(external.getMarca())
                .modelo(external.getModelo())
                .color(external.getColor())
                .tipo("MOTO".equals(external.getTipo()) ? VehicleType.MOTO : VehicleType.AUTO)
                .active(true)
                .build();
        vehicleRepository.save(vehicle);
    }
}
