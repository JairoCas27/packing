package com.urbanpark.parking.service;

import com.urbanpark.parking.config.SuperAdminProperties;
import com.urbanpark.parking.domain.entity.SaasUser;
import com.urbanpark.parking.domain.enums.SaasRole;
import com.urbanpark.parking.domain.repository.SaasUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final SaasUserRepository saasUserRepository;
    private final SuperAdminProperties superAdminProperties;
    private final PasswordEncoder passwordEncoder;

    @EventListener(ApplicationReadyEvent.class)
    public void initSuperAdmin() {
        if (!saasUserRepository.existsByEmail(superAdminProperties.getEmail())) {
            SaasUser superAdmin = SaasUser.builder()
                    .email(superAdminProperties.getEmail())
                    .passwordHash(passwordEncoder.encode(superAdminProperties.getPassword()))
                    .role(SaasRole.SUPERADMIN)
                    .active(true)
                    .build();
            saasUserRepository.save(superAdmin);
            log.info("Superadmin inicial creado: {}", superAdminProperties.getEmail());
        }
    }
}
