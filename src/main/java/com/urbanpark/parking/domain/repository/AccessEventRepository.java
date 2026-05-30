package com.urbanpark.parking.domain.repository;

import com.urbanpark.parking.domain.entity.AccessEvent;
import com.urbanpark.parking.domain.enums.AccessStatus;
import com.urbanpark.parking.domain.enums.AccessType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AccessEventRepository extends JpaRepository<AccessEvent, Long> {
    List<AccessEvent> findByTenantIdOrderByCreatedAtDesc(Long tenantId);

    @Query("SELECT a FROM AccessEvent a WHERE a.tenant.id = :tenantId AND a.placa = :placa " +
           "AND a.type = 'ENTRY' AND a.exitTimestamp IS NULL ORDER BY a.entryTimestamp DESC")
    Optional<AccessEvent> findOpenEntry(@Param("tenantId") Long tenantId, @Param("placa") String placa);

    List<AccessEvent> findByTenantIdAndParkingUserIdOrderByCreatedAtDesc(Long tenantId, Long parkingUserId);

    List<AccessEvent> findByTenantIdAndCreatedAtBetweenOrderByCreatedAtDesc(
            Long tenantId, LocalDateTime from, LocalDateTime to);

    long countByTenantIdAndTypeAndStatusAndCreatedAtBetween(
            Long tenantId, AccessType type, AccessStatus status, LocalDateTime from, LocalDateTime to);
}
