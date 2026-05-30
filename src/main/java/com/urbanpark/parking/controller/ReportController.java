package com.urbanpark.parking.controller;

import com.urbanpark.parking.dto.response.AccessEventResponse;
import com.urbanpark.parking.dto.response.AuditLogResponse;
import com.urbanpark.parking.dto.response.NotificationResponse;
import com.urbanpark.parking.dto.response.OccupancyReportResponse;
import com.urbanpark.parking.service.AuditQueryService;
import com.urbanpark.parking.service.NotificationService;
import com.urbanpark.parking.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "Reportes y auditoría")
public class ReportController {

    private final ReportService reportService;
    private final AuditQueryService auditQueryService;
    private final NotificationService notificationService;

    @GetMapping("/api/reports/access")
    @Operation(summary = "Reporte de accesos por rango de fechas")
    public ResponseEntity<List<AccessEventResponse>> accessReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return ResponseEntity.ok(reportService.accessReport(from, to));
    }

    @GetMapping("/api/reports/access/summary")
    @Operation(summary = "Resumen de accesos")
    public ResponseEntity<Map<String, Object>> accessSummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return ResponseEntity.ok(reportService.accessSummary(from, to));
    }

    @GetMapping("/api/reports/occupancy")
    @Operation(summary = "Reporte de ocupación")
    public ResponseEntity<OccupancyReportResponse> occupancyReport() {
        return ResponseEntity.ok(reportService.occupancyReport());
    }

    @GetMapping("/api/audit/tenant/{tenantId}")
    @Operation(summary = "Auditoría por condominio")
    public ResponseEntity<List<AuditLogResponse>> tenantAudit(
            @PathVariable Long tenantId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return ResponseEntity.ok(auditQueryService.listByTenant(tenantId, from, to));
    }

    @GetMapping("/api/audit/global")
    @Operation(summary = "Auditoría global del SaaS")
    public ResponseEntity<List<AuditLogResponse>> globalAudit() {
        return ResponseEntity.ok(auditQueryService.listGlobal());
    }

    @GetMapping("/api/notifications")
    @Operation(summary = "Mis notificaciones")
    public ResponseEntity<List<NotificationResponse>> notifications() {
        return ResponseEntity.ok(notificationService.myNotifications());
    }

    @PatchMapping("/api/notifications/{id}/read")
    @Operation(summary = "Marcar notificación como leída")
    public ResponseEntity<NotificationResponse> markRead(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.markAsRead(id));
    }
}
