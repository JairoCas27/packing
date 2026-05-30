package com.urbanpark.parking.service;

import com.urbanpark.parking.domain.enums.ParkingRole;
import com.urbanpark.parking.exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class RoleMapper {

    public ParkingRole mapExternalRole(String externalRole) {
        if (externalRole == null) {
            throw new ApiException("Rol externo no definido", HttpStatus.BAD_REQUEST);
        }
        return switch (externalRole) {
            case "ADMINISTRADOR_CONDOMINIO" -> ParkingRole.ADMIN_CONDOMINIO;
            case "PROPIETARIO" -> ParkingRole.PROPIETARIO;
            case "AGENTE_SEGURIDAD" -> ParkingRole.AGENTE_SEGURIDAD;
            case "SUPER_ADMINISTRADOR" -> ParkingRole.ADMIN_CONDOMINIO;
            default -> throw new ApiException("Rol no soportado en parking: " + externalRole, HttpStatus.FORBIDDEN);
        };
    }
}
