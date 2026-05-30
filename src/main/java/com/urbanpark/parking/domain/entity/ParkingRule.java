package com.urbanpark.parking.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "parking_rules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParkingRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant_id", nullable = false, unique = true)
    private Tenant tenant;

    @Column(name = "max_vehicles_per_user")
    @Builder.Default
    private int maxVehiclesPerUser = 2;

    @Column(name = "max_visitors_per_day")
    @Builder.Default
    private int maxVisitorsPerDay = 5;

    @Column(name = "access_start_time")
    private LocalTime accessStartTime;

    @Column(name = "access_end_time")
    private LocalTime accessEndTime;

    @Column(name = "allow_visitors_outside_hours")
    @Builder.Default
    private boolean allowVisitorsOutsideHours = false;

    @Column(name = "max_autos")
    @Builder.Default
    private int maxAutos = 10;

    @Column(name = "max_motos")
    @Builder.Default
    private int maxMotos = 10;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
