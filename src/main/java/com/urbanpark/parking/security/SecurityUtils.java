package com.urbanpark.parking.security;

import com.urbanpark.parking.exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    public UserPrincipal getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UserPrincipal principal)) {
            throw new ApiException("Usuario no autenticado", HttpStatus.UNAUTHORIZED);
        }
        return principal;
    }

    public Long requireTenantId() {
        UserPrincipal user = getCurrentUser();
        if (user.getTenantId() == null) {
            throw new ApiException("Operación requiere contexto de condominio", HttpStatus.BAD_REQUEST);
        }
        return user.getTenantId();
    }

    public void requireSaasRole(String... roles) {
        UserPrincipal user = getCurrentUser();
        if (!user.isSaasUser()) {
            throw new ApiException("Acceso restringido a administradores SaaS", HttpStatus.FORBIDDEN);
        }
        for (String role : roles) {
            if (role.equals(user.getRole())) {
                return;
            }
        }
        throw new ApiException("No tiene permisos suficientes", HttpStatus.FORBIDDEN);
    }

    public void requireParkingRole(String... roles) {
        UserPrincipal user = getCurrentUser();
        if (user.isSaasUser()) {
            return;
        }
        for (String role : roles) {
            if (role.equals(user.getRole())) {
                return;
            }
        }
        throw new ApiException("No tiene permisos suficientes", HttpStatus.FORBIDDEN);
    }
}
