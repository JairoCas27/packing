package com.urbanpark.parking.controller;

import com.urbanpark.parking.dto.request.UpdateParkingRuleRequest;
import com.urbanpark.parking.dto.response.ParkingRuleResponse;
import com.urbanpark.parking.service.ParkingRuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/parking/rules")
@RequiredArgsConstructor
@Tag(name = "Reglas de acceso")
public class ParkingRuleController {

    private final ParkingRuleService parkingRuleService;

    @GetMapping
    @Operation(summary = "Obtener reglas del condominio")
    public ResponseEntity<ParkingRuleResponse> get() {
        return ResponseEntity.ok(parkingRuleService.getRules());
    }

    @PutMapping
    @Operation(summary = "Actualizar reglas del condominio")
    public ResponseEntity<ParkingRuleResponse> update(@RequestBody UpdateParkingRuleRequest request) {
        return ResponseEntity.ok(parkingRuleService.updateRules(request));
    }
}
