package com.urbanpark.parking.service;

import com.urbanpark.parking.domain.enums.AccessStatus;
import com.urbanpark.parking.domain.enums.AccessType;
import com.urbanpark.parking.domain.repository.AccessEventRepository;
import com.urbanpark.parking.dto.response.AccessEventResponse;
import com.urbanpark.parking.dto.response.OccupancyReportResponse;
import com.urbanpark.parking.mapper.ResponseMapper;
import com.urbanpark.parking.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final AccessEventRepository accessEventRepository;
    private final ParkingSpaceService parkingSpaceService;
    private final ResponseMapper responseMapper;
    private final SecurityUtils securityUtils;

    @Transactional(readOnly = true)
    public List<AccessEventResponse> accessReport(LocalDateTime from, LocalDateTime to) {
        Long tenantId = securityUtils.requireTenantId();
        securityUtils.requireParkingRole("ADMIN_CONDOMINIO", "AGENTE_SEGURIDAD");
        return accessEventRepository
                .findByTenantIdAndCreatedAtBetweenOrderByCreatedAtDesc(tenantId, from, to)
                .stream().map(responseMapper::toAccessEventResponse).toList();
    }

    @Transactional(readOnly = true)
    public OccupancyReportResponse occupancyReport() {
        securityUtils.requireParkingRole("ADMIN_CONDOMINIO", "AGENTE_SEGURIDAD");
        return parkingSpaceService.occupancy();
    }

    @Transactional(readOnly = true)
    public Map<String, Object> accessSummary(LocalDateTime from, LocalDateTime to) {
        Long tenantId = securityUtils.requireTenantId();
        securityUtils.requireParkingRole("ADMIN_CONDOMINIO");

        long entries = accessEventRepository.countByTenantIdAndTypeAndStatusAndCreatedAtBetween(
                tenantId, AccessType.ENTRY, AccessStatus.AUTHORIZED, from, to);
        long denied = accessEventRepository.countByTenantIdAndTypeAndStatusAndCreatedAtBetween(
                tenantId, AccessType.ENTRY, AccessStatus.DENIED, from, to);
        long exits = accessEventRepository.countByTenantIdAndTypeAndStatusAndCreatedAtBetween(
                tenantId, AccessType.EXIT, AccessStatus.AUTHORIZED, from, to);

        Map<String, Object> summary = new HashMap<>();
        summary.put("authorizedEntries", entries);
        summary.put("deniedEntries", denied);
        summary.put("exits", exits);
        summary.put("from", from);
        summary.put("to", to);
        return summary;
    }
}
