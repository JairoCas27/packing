package com.urbanpark.parking.service;

import com.urbanpark.parking.domain.entity.ParkingUser;
import com.urbanpark.parking.domain.entity.Tenant;
import com.urbanpark.parking.domain.enums.ParkingRole;
import com.urbanpark.parking.domain.repository.ParkingUserRepository;
import com.urbanpark.parking.integration.core.dto.CoreUsuarioResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ParkingUserSyncService {

    private final ParkingUserRepository parkingUserRepository;
    private final RoleMapper roleMapper;

    @Transactional
    public ParkingUser syncUser(Tenant tenant, CoreUsuarioResponse externalUser) {
        ParkingRole role = roleMapper.mapExternalRole(externalUser.getRol());
        ParkingUser user = parkingUserRepository
                .findByTenantIdAndExternalUserId(tenant.getId(), externalUser.getId())
                .orElse(ParkingUser.builder()
                        .tenant(tenant)
                        .externalUserId(externalUser.getId())
                        .build());

        user.setEmail(externalUser.getCorreo());
        user.setNombres(externalUser.getNombres());
        user.setApellidos(externalUser.getApellidos());
        user.setTelefono(externalUser.getTelefono());
        user.setRole(role);
        user.setActive(Boolean.TRUE.equals(externalUser.getActivo()));

        return parkingUserRepository.save(user);
    }
}
