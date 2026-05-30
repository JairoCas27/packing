package com.urbanpark.parking.controller;

import com.urbanpark.parking.dto.request.CreateTenantRequest;
import com.urbanpark.parking.dto.request.UpdateTenantRequest;
import com.urbanpark.parking.dto.response.TenantResponse;
import com.urbanpark.parking.service.CondominioSyncService;
import com.urbanpark.parking.service.TenantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tenants")
@RequiredArgsConstructor
@Tag(name = "Condominios (Tenants)")
public class TenantController {

    private final TenantService tenantService;
    private final CondominioSyncService condominioSyncService;

    @PostMapping
    @Operation(summary = "Registrar condominio como tenant")
    public ResponseEntity<TenantResponse> create(@Valid @RequestBody CreateTenantRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tenantService.create(request));
    }

    @GetMapping
    @Operation(summary = "Listar todos los condominios")
    public ResponseEntity<List<TenantResponse>> list() {
        return ResponseEntity.ok(tenantService.listAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener condominio por ID")
    public ResponseEntity<TenantResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(tenantService.getById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar configuración del condominio")
    public ResponseEntity<TenantResponse> update(@PathVariable Long id,
                                                 @RequestBody UpdateTenantRequest request) {
        return ResponseEntity.ok(tenantService.update(id, request));
    }

    @PostMapping("/{id}/sync")
    @Operation(summary = "Sincronizar datos con API del condominio")
    public ResponseEntity<Void> sync(@PathVariable Long id) {
        condominioSyncService.syncTenant(id);
        return ResponseEntity.accepted().build();
    }
}
