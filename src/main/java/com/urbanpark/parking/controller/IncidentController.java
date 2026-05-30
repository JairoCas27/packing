package com.urbanpark.parking.controller;

import com.urbanpark.parking.domain.entity.SecurityIncident;
import com.urbanpark.parking.dto.request.CreateIncidentRequest;
import com.urbanpark.parking.service.IncidentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/incidents")
@RequiredArgsConstructor
@Tag(name = "Incidentes de seguridad")
public class IncidentController {

    private final IncidentService incidentService;

    @PostMapping
    @Operation(summary = "Registrar incidente de seguridad")
    public ResponseEntity<SecurityIncident> create(@Valid @RequestBody CreateIncidentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(incidentService.create(request));
    }

    @GetMapping
    @Operation(summary = "Listar incidentes del condominio")
    public ResponseEntity<List<SecurityIncident>> list() {
        return ResponseEntity.ok(incidentService.list());
    }
}
