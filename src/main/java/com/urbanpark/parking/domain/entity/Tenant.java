package com.urbanpark.parking.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "tenants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tenant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "external_condominio_id", nullable = false, unique = true)
    private Long externalCondominioId;

    @Column(name = "api_base_url", nullable = false)
    private String apiBaseUrl;

    @Column(name = "api_key")
    private String apiKey;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    private String country;
    private String city;
    private String address;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
