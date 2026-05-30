package com.urbanpark.parking.service;

import com.urbanpark.parking.config.JwtProperties;
import com.urbanpark.parking.domain.entity.ParkingUser;
import com.urbanpark.parking.domain.entity.SaasUser;
import com.urbanpark.parking.domain.entity.Tenant;
import com.urbanpark.parking.domain.repository.SaasUserRepository;
import com.urbanpark.parking.domain.repository.TenantRepository;
import com.urbanpark.parking.dto.request.CondominioLoginRequest;
import com.urbanpark.parking.dto.request.SaasLoginRequest;
import com.urbanpark.parking.dto.request.TokenExchangeRequest;
import com.urbanpark.parking.dto.response.AuthResponse;
import com.urbanpark.parking.dto.response.UserInfoResponse;
import com.urbanpark.parking.exception.ApiException;
import com.urbanpark.parking.integration.core.CoreApiClient;
import com.urbanpark.parking.integration.core.dto.CoreAuthResponse;
import com.urbanpark.parking.integration.core.dto.CoreUsuarioResponse;
import com.urbanpark.parking.security.JwtTokenProvider;
import com.urbanpark.parking.security.UserPrincipal;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final SaasUserRepository saasUserRepository;
    private final TenantRepository tenantRepository;
    private final CoreApiClient coreApiClient;
    private final ParkingUserSyncService parkingUserSyncService;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtProperties jwtProperties;
    private final PasswordEncoder passwordEncoder;
    private final AuditService auditService;

    @Transactional(readOnly = true)
    public AuthResponse saasLogin(SaasLoginRequest request) {
        SaasUser user = saasUserRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ApiException("Credenciales inválidas", HttpStatus.UNAUTHORIZED));

        if (!user.isActive() || !passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new ApiException("Credenciales inválidas", HttpStatus.UNAUTHORIZED);
        }

        String token = jwtTokenProvider.generateToken(
                user.getId().toString(), user.getEmail(), user.getRole().name(), null, true);

        UserPrincipal actor = new UserPrincipal(
                user.getId().toString(), user.getEmail(), user.getRole().name(), null, null, true, true);
        auditService.log(actor, null, "SAAS_LOGIN", "SaasUser", user.getId().toString(), null);

        return buildAuthResponse(token, UserInfoResponse.builder()
                .id(user.getId().toString())
                .email(user.getEmail())
                .role(user.getRole().name())
                .saasUser(true)
                .build());
    }

    @Transactional
    public AuthResponse condominioLogin(CondominioLoginRequest request) {
        Tenant tenant = getActiveTenant(request.getTenantId());
        CoreAuthResponse coreAuth = coreApiClient.login(
                tenant.getApiBaseUrl(), request.getEmail(), request.getPassword());

        if (coreAuth.getUser() == null) {
            throw new ApiException("Respuesta inválida del sistema del condominio", HttpStatus.BAD_GATEWAY);
        }

        validateTenantMatch(tenant, coreAuth.getUser());
        ParkingUser parkingUser = parkingUserSyncService.syncUser(tenant, coreAuth.getUser());

        if (!parkingUser.isActive()) {
            throw new ApiException("Usuario inactivo", HttpStatus.FORBIDDEN);
        }

        return buildParkingAuth(tenant, parkingUser);
    }

    @Transactional
    public AuthResponse exchangeToken(TokenExchangeRequest request) {
        Tenant tenant = getActiveTenant(request.getTenantId());
        String userId = extractUserIdFromToken(request.getCondominioToken());
        CoreUsuarioResponse externalUser = coreApiClient.getUsuario(
                tenant.getApiBaseUrl(), request.getCondominioToken(), userId);

        validateTenantMatch(tenant, externalUser);
        ParkingUser parkingUser = parkingUserSyncService.syncUser(tenant, externalUser);

        if (!parkingUser.isActive()) {
            throw new ApiException("Usuario inactivo", HttpStatus.FORBIDDEN);
        }

        return buildParkingAuth(tenant, parkingUser);
    }

    private AuthResponse buildParkingAuth(Tenant tenant, ParkingUser parkingUser) {
        String token = jwtTokenProvider.generateToken(
                parkingUser.getId().toString(),
                parkingUser.getEmail(),
                parkingUser.getRole().name(),
                tenant.getId(),
                false);

        UserPrincipal actor = new UserPrincipal(
                parkingUser.getId().toString(), parkingUser.getEmail(),
                parkingUser.getRole().name(), tenant.getId(), null, true, false);
        auditService.log(actor, tenant.getId(), "CONDOMINIO_LOGIN", "ParkingUser",
                parkingUser.getId().toString(), null);

        return buildAuthResponse(token, UserInfoResponse.builder()
                .id(parkingUser.getId().toString())
                .email(parkingUser.getEmail())
                .nombres(parkingUser.getNombres())
                .apellidos(parkingUser.getApellidos())
                .role(parkingUser.getRole().name())
                .tenantId(tenant.getId())
                .tenantName(tenant.getName())
                .saasUser(false)
                .build());
    }

    private AuthResponse buildAuthResponse(String token, UserInfoResponse user) {
        return AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(jwtProperties.getExpirationMs() / 1000)
                .user(user)
                .build();
    }

    private Tenant getActiveTenant(Long tenantId) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ApiException("Condominio no registrado en el SaaS", HttpStatus.NOT_FOUND));
        if (!tenant.isActive()) {
            throw new ApiException("Condominio inactivo", HttpStatus.FORBIDDEN);
        }
        return tenant;
    }

    private void validateTenantMatch(Tenant tenant, CoreUsuarioResponse user) {
        if (user.getCondominioId() != null && !user.getCondominioId().equals(tenant.getExternalCondominioId())) {
            throw new ApiException("El usuario no pertenece a este condominio", HttpStatus.FORBIDDEN);
        }
    }

    private String extractUserIdFromToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length < 2) {
                throw new ApiException("Token JWT inválido", HttpStatus.BAD_REQUEST);
            }
            String payload = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            if (payload.contains("\"sub\"")) {
                int start = payload.indexOf("\"sub\"") + 7;
                int end = payload.indexOf("\"", start);
                return payload.substring(start, end);
            }
            if (payload.contains("\"userId\"")) {
                int start = payload.indexOf("\"userId\"") + 10;
                int end = payload.indexOf("\"", start);
                return payload.substring(start, end);
            }
            throw new ApiException("No se pudo extraer userId del token", HttpStatus.BAD_REQUEST);
        } catch (ApiException ex) {
            throw ex;
        } catch (Exception ex) {
            Claims claims = jwtTokenProvider.parseToken(token);
            String userId = claims.get("userId", String.class);
            if (userId == null) {
                userId = claims.getSubject();
            }
            return userId;
        }
    }
}
