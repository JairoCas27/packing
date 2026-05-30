package com.urbanpark.parking.controller;

import com.urbanpark.parking.dto.request.*;
import com.urbanpark.parking.dto.response.AuthResponse;
import com.urbanpark.parking.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/saas/login")
    @Operation(summary = "Login SUPERADMIN / ADMIN del SaaS")
    public ResponseEntity<AuthResponse> saasLogin(@Valid @RequestBody SaasLoginRequest request) {
        return ResponseEntity.ok(authService.saasLogin(request));
    }

    @PostMapping("/condominio/login")
    @Operation(summary = "Login vía sistema del condominio (proxy)")
    public ResponseEntity<AuthResponse> condominioLogin(@Valid @RequestBody CondominioLoginRequest request) {
        return ResponseEntity.ok(authService.condominioLogin(request));
    }

    @PostMapping("/exchange")
    @Operation(summary = "Intercambiar JWT del condominio por JWT interno del SaaS")
    public ResponseEntity<AuthResponse> exchangeToken(@Valid @RequestBody TokenExchangeRequest request) {
        return ResponseEntity.ok(authService.exchangeToken(request));
    }
}
