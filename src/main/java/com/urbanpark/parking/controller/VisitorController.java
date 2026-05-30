package com.urbanpark.parking.controller;

import com.urbanpark.parking.dto.request.CreateVisitorRequest;
import com.urbanpark.parking.dto.response.VisitorResponse;
import com.urbanpark.parking.service.VisitorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/visitors")
@RequiredArgsConstructor
@Tag(name = "Visitantes")
public class VisitorController {

    private final VisitorService visitorService;

    @PostMapping
    @Operation(summary = "Registrar visitante temporal")
    public ResponseEntity<VisitorResponse> create(@Valid @RequestBody CreateVisitorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(visitorService.create(request));
    }

    @GetMapping
    @Operation(summary = "Listar visitantes")
    public ResponseEntity<List<VisitorResponse>> list() {
        return ResponseEntity.ok(visitorService.list());
    }
}
