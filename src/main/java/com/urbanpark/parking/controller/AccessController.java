package com.urbanpark.parking.controller;

import com.urbanpark.parking.dto.request.RegisterAccessRequest;
import com.urbanpark.parking.dto.response.AccessEventResponse;
import com.urbanpark.parking.service.AccessService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/access")
@RequiredArgsConstructor
@Tag(name = "Control de accesos")
public class AccessController {

    private final AccessService accessService;

    @PostMapping("/entry")
    @Operation(summary = "Registrar entrada vehicular")
    public ResponseEntity<AccessEventResponse> entry(@Valid @RequestBody RegisterAccessRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(accessService.registerEntry(request));
    }

    @PostMapping("/exit")
    @Operation(summary = "Registrar salida vehicular")
    public ResponseEntity<AccessEventResponse> exit(@Valid @RequestBody RegisterAccessRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(accessService.registerExit(request));
    }

    @GetMapping("/validate/{placa}")
    @Operation(summary = "Validar placa manualmente")
    public ResponseEntity<AccessEventResponse> validate(@PathVariable String placa) {
        return ResponseEntity.ok(accessService.validatePlate(placa));
    }

    @GetMapping("/events")
    @Operation(summary = "Historial de accesos")
    public ResponseEntity<List<AccessEventResponse>> events() {
        return ResponseEntity.ok(accessService.listEvents());
    }
}
