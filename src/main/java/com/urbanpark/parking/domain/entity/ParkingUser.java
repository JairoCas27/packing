package com.urbanpark.parking.domain.entity;

import com.urbanpark.parking.domain.enums.ParkingRole;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "parking_users", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"tenant_id", "external_user_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParkingUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Column(name = "external_user_id", nullable = false)
    private String externalUserId;

    @Column(nullable = false)
    private String email;

    private String nombres;
    private String apellidos;
    private String telefono;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ParkingRole role;

    @Column(name = "external_apartamento_id")
    private Long externalApartamentoId;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
