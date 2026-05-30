package com.urbanpark.parking.service;

import com.urbanpark.parking.domain.entity.ParkingRule;
import com.urbanpark.parking.domain.entity.Tenant;
import com.urbanpark.parking.domain.repository.ParkingRuleRepository;
import com.urbanpark.parking.domain.repository.TenantRepository;
import com.urbanpark.parking.dto.request.CreateTenantRequest;
import com.urbanpark.parking.dto.request.UpdateTenantRequest;
import com.urbanpark.parking.dto.response.TenantResponse;
import com.urbanpark.parking.exception.ApiException;
import com.urbanpark.parking.mapper.ResponseMapper;
import com.urbanpark.parking.security.SecurityUtils;
import com.urbanpark.parking.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TenantService {

    private final TenantRepository tenantRepository;
    private final ParkingRuleRepository parkingRuleRepository;
    private final ResponseMapper responseMapper;
    private final SecurityUtils securityUtils;
    private final AuditService auditService;

    @Value("${core.api.url}")
    private String defaultCoreApiUrl;

    @Transactional
    public TenantResponse create(CreateTenantRequest request) {
        securityUtils.requireSaasRole("SUPERADMIN");

        if (tenantRepository.existsByExternalCondominioId(request.getExternalCondominioId())) {
            throw new ApiException("El condominio ya está registrado", HttpStatus.CONFLICT);
        }

        Tenant tenant = Tenant.builder()
                .name(request.getName())
                .externalCondominioId(request.getExternalCondominioId())
                .apiBaseUrl(request.getApiBaseUrl() != null ? request.getApiBaseUrl() : defaultCoreApiUrl)
                .apiKey(request.getApiKey())
                .country(request.getCountry())
                .city(request.getCity())
                .address(request.getAddress())
                .active(true)
                .build();

        tenant = tenantRepository.save(tenant);

        parkingRuleRepository.save(ParkingRule.builder().tenant(tenant).build());

        UserPrincipal actor = securityUtils.getCurrentUser();
        auditService.log(actor, tenant.getId(), "TENANT_CREATED", "Tenant",
                tenant.getId().toString(), tenant.getName());

        return responseMapper.toTenantResponse(tenant);
    }

    @Transactional(readOnly = true)
    public List<TenantResponse> listAll() {
        securityUtils.requireSaasRole("SUPERADMIN", "ADMIN");
        return tenantRepository.findAll().stream()
                .map(responseMapper::toTenantResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public TenantResponse getById(Long id) {
        UserPrincipal user = securityUtils.getCurrentUser();
        if (user.isSaasUser()) {
            securityUtils.requireSaasRole("SUPERADMIN", "ADMIN");
        } else if (!id.equals(user.getTenantId())) {
            throw new ApiException("No puede acceder a otro condominio", HttpStatus.FORBIDDEN);
        }
        return responseMapper.toTenantResponse(findTenant(id));
    }

    @Transactional
    public TenantResponse update(Long id, UpdateTenantRequest request) {
        securityUtils.requireSaasRole("SUPERADMIN");
        Tenant tenant = findTenant(id);

        if (request.getName() != null) tenant.setName(request.getName());
        if (request.getApiBaseUrl() != null) tenant.setApiBaseUrl(request.getApiBaseUrl());
        if (request.getApiKey() != null) tenant.setApiKey(request.getApiKey());
        if (request.getActive() != null) tenant.setActive(request.getActive());
        if (request.getCountry() != null) tenant.setCountry(request.getCountry());
        if (request.getCity() != null) tenant.setCity(request.getCity());
        if (request.getAddress() != null) tenant.setAddress(request.getAddress());

        tenant = tenantRepository.save(tenant);

        UserPrincipal actor = securityUtils.getCurrentUser();
        auditService.log(actor, tenant.getId(), "TENANT_UPDATED", "Tenant",
                tenant.getId().toString(), null);

        return responseMapper.toTenantResponse(tenant);
    }

    public Tenant findTenant(Long id) {
        return tenantRepository.findById(id)
                .orElseThrow(() -> new ApiException("Condominio no encontrado", HttpStatus.NOT_FOUND));
    }
}
