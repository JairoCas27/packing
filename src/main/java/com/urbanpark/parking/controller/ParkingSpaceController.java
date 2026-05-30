package com.urbanpark.parking.controller;

import com.urbanpark.parking.dto.request.CreateParkingSpaceRequest;
import com.urbanpark.parking.dto.request.UpdateParkingSpaceRequest;
import com.urbanpark.parking.dto.response.OccupancyReportResponse;
import com.urbanpark.parking.dto.response.ParkingSpaceResponse;
import com.urbanpark.parking.service.ParkingSpaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parking/spaces")
@RequiredArgsConstructor
@Tag(name = "Estacionamientos")
public class ParkingSpaceController {

    private final ParkingSpaceService parkingSpaceService;

    @PostMapping
    @Operation(summary = "Crear espacio de estacionamiento")
    public ResponseEntity<ParkingSpaceResponse> create(@Valid @RequestBody CreateParkingSpaceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(parkingSpaceService.create(request));
    }

    @GetMapping
    @Operation(summary = "Listar espacios del condominio")
    public ResponseEntity<List<ParkingSpaceResponse>> list() {
        return ResponseEntity.ok(parkingSpaceService.list());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar espacio")
    public ResponseEntity<ParkingSpaceResponse> update(@PathVariable Long id,
                                                       @RequestBody UpdateParkingSpaceRequest request) {
        return ResponseEntity.ok(parkingSpaceService.update(id, request));
    }

    @GetMapping("/occupancy")
    @Operation(summary = "Consultar ocupación en tiempo real")
    public ResponseEntity<OccupancyReportResponse> occupancy() {
        return ResponseEntity.ok(parkingSpaceService.occupancy());
    }
}
