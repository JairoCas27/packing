package com.urbanpark.parking.domain.repository;

import com.urbanpark.parking.domain.entity.SaasUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SaasUserRepository extends JpaRepository<SaasUser, UUID> {
    Optional<SaasUser> findByEmail(String email);
    boolean existsByEmail(String email);
}
