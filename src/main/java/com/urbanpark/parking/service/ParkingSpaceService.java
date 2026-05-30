package com.urbanpark.parking.service;

import com.urbanpark.parking.domain.entity.ParkingSpace;
import com.urbanpark.parking.domain.entity.ParkingUser;
import com.urbanpark.parking.domain.entity.Tenant;
import com.urbanpark.parking.domain.enums.SpaceStatus;
import com.urbanpark.parking.domain.repository.ParkingSpaceRepository;
import com.urbanpark.parking.domain.repository.ParkingUserRepository;
import com.urbanpark.parking.dto.request.CreateParkingSpaceRequest;
import com.urbanpark.parking.dto.request.UpdateParkingSpaceRequest;
import com.urbanpark.parking.dto.response.OccupancyReportResponse;
import com.urbanpark.parking.dto.response.ParkingSpaceResponse;
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
public class ParkingSpaceService {

    private final ParkingSpaceRepository parkingSpaceRepository;
    private final ParkingUserRepository parkingUserRepository;
    private final TenantService tenantService;
    private final ResponseMapper responseMapper;
    private final SecurityUtils securityUtils;
    private final AuditService auditService;

    @Transactional
    public ParkingSpaceResponse create(CreateParkingSpaceRequest request) {
        securityUtils.requireParkingRole("ADMIN_CONDOMINIO");
        Long tenantId = securityUtils.requireTenantId();
        Tenant tenant = tenantService.findTenant(tenantId);

        if (parkingSpaceRepository.findByTenantIdAndCode(tenantId, request.getCode()).isPresent()) {
            throw new ApiException("Ya existe un espacio con ese código", HttpStatus.CONFLICT);
        }

        ParkingSpace space = ParkingSpace.builder()
                .tenant(tenant)
                .code(request.getCode())
                .zone(request.getZone())
                .type(request.getType())
                .status(SpaceStatus.AVAILABLE)
                .build();

        space = parkingSpaceRepository.save(space);
        audit(securityUtils.getCurrentUser(), tenantId, "PARKING_SPACE_CREATED", space.getId());
        return responseMapper.toParkingSpaceResponse(space);
    }

    @Transactional(readOnly = true)
    public List<ParkingSpaceResponse> list() {
        Long tenantId = securityUtils.requireTenantId();
        return parkingSpaceRepository.findByTenantId(tenantId).stream()
                .map(responseMapper::toParkingSpaceResponse)
                .toList();
    }

    @Transactional
    public ParkingSpaceResponse update(Long id, UpdateParkingSpaceRequest request) {
        securityUtils.requireParkingRole("ADMIN_CONDOMINIO");
        Long tenantId = securityUtils.requireTenantId();
        ParkingSpace space = findByTenant(tenantId, id);

        if (request.getCode() != null) space.setCode(request.getCode());
        if (request.getZone() != null) space.setZone(request.getZone());
        if (request.getType() != null) space.setType(request.getType());
        if (request.getStatus() != null) space.setStatus(request.getStatus());
        if (request.getAssignedUserId() != null) {
            ParkingUser user = parkingUserRepository.findById(request.getAssignedUserId())
                    .filter(u -> u.getTenant().getId().equals(tenantId))
                    .orElseThrow(() -> new ApiException("Usuario no encontrado", HttpStatus.NOT_FOUND));
            space.setAssignedUser(user);
        }

        space = parkingSpaceRepository.save(space);
        audit(securityUtils.getCurrentUser(), tenantId, "PARKING_SPACE_UPDATED", space.getId());
        return responseMapper.toParkingSpaceResponse(space);
    }

    @Transactional(readOnly = true)
    public OccupancyReportResponse occupancy() {
        Long tenantId = securityUtils.requireTenantId();
        long total = parkingSpaceRepository.findByTenantId(tenantId).size();
        long available = parkingSpaceRepository.countByTenantIdAndStatus(tenantId, SpaceStatus.AVAILABLE);
        long occupied = parkingSpaceRepository.countByTenantIdAndStatus(tenantId, SpaceStatus.OCCUPIED);
        long reserved = parkingSpaceRepository.countByTenantIdAndStatus(tenantId, SpaceStatus.RESERVED);
        long maintenance = parkingSpaceRepository.countByTenantIdAndStatus(tenantId, SpaceStatus.MAINTENANCE);

        return OccupancyReportResponse.builder()
                .totalSpaces(total)
                .available(available)
                .occupied(occupied)
                .reserved(reserved)
                .maintenance(maintenance)
                .occupancyRate(total == 0 ? 0 : (double) occupied / total * 100)
                .build();
    }

    public ParkingSpace findAvailableSpace(Long tenantId, com.urbanpark.parking.domain.enums.SpaceType type) {
        return parkingSpaceRepository.findByTenantIdAndTypeAndStatus(tenantId, type, SpaceStatus.AVAILABLE)
                .stream()
                .findFirst()
                .orElse(null);
    }

    public ParkingSpace findByTenant(Long tenantId, Long id) {
        return parkingSpaceRepository.findById(id)
                .filter(s -> s.getTenant().getId().equals(tenantId))
                .orElseThrow(() -> new ApiException("Espacio no encontrado", HttpStatus.NOT_FOUND));
    }

    private void audit(UserPrincipal actor, Long tenantId, String action, Long entityId) {
        auditService.log(actor, tenantId, action, "ParkingSpace", entityId.toString(), null);
    }
}
