package com.urbanpark.parking.domain.entity;

import com.urbanpark.parking.domain.enums.AccessMethod;
import com.urbanpark.parking.domain.enums.AccessStatus;
import com.urbanpark.parking.domain.enums.AccessType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "access_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccessEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Column(nullable = false)
    private String placa;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccessType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccessMethod method;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccessStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visitor_id")
    private Visitor visitor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parking_user_id")
    private ParkingUser parkingUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parking_space_id")
    private ParkingSpace parkingSpace;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registered_by_id")
    private ParkingUser registeredBy;

    @Column(name = "denial_reason")
    private String denialReason;

    @Column(name = "entry_timestamp")
    private LocalDateTime entryTimestamp;

    @Column(name = "exit_timestamp")
    private LocalDateTime exitTimestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "linked_entry_id")
    private AccessEvent linkedEntry;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
