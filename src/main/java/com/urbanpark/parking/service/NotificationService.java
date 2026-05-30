package com.urbanpark.parking.service;

import com.urbanpark.parking.domain.entity.*;
import com.urbanpark.parking.domain.enums.NotificationType;
import com.urbanpark.parking.domain.repository.NotificationRepository;
import com.urbanpark.parking.domain.repository.ParkingUserRepository;
import com.urbanpark.parking.dto.response.NotificationResponse;
import com.urbanpark.parking.mapper.ResponseMapper;
import com.urbanpark.parking.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final ParkingUserRepository parkingUserRepository;
    private final ResponseMapper responseMapper;
    private final SecurityUtils securityUtils;

    @Transactional
    public void notifyAccess(ParkingUser recipient, AccessEvent event) {
        save(recipient, "Ingreso de vehículo",
                "Su vehículo " + event.getPlaca() + " ingresó al estacionamiento.",
                NotificationType.VEHICLE_ENTRY);
    }

    @Transactional
    public void notifyExit(ParkingUser recipient, AccessEvent event) {
        save(recipient, "Salida de vehículo",
                "Su vehículo " + event.getPlaca() + " salió del estacionamiento.",
                NotificationType.VEHICLE_EXIT);
    }

    @Transactional
    public void notifyUnauthorized(Tenant tenant, String placa) {
        parkingUserRepository.findByTenantIdAndRole(tenant.getId(),
                        com.urbanpark.parking.domain.enums.ParkingRole.ADMIN_CONDOMINIO)
                .forEach(admin -> save(admin, "Acceso no autorizado",
                        "Intento de ingreso denegado para placa " + placa + ".",
                        NotificationType.UNAUTHORIZED_ACCESS));
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> myNotifications() {
        Long userId = Long.parseLong(securityUtils.getCurrentUser().getUserId());
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId).stream()
                .map(responseMapper::toNotificationResponse)
                .toList();
    }

    @Transactional
    public NotificationResponse markAsRead(Long id) {
        Long userId = Long.parseLong(securityUtils.getCurrentUser().getUserId());
        Notification notification = notificationRepository.findById(id)
                .filter(n -> n.getRecipient().getId().equals(userId))
                .orElseThrow(() -> new com.urbanpark.parking.exception.ApiException(
                        "Notificación no encontrada", org.springframework.http.HttpStatus.NOT_FOUND));
        notification.setRead(true);
        return responseMapper.toNotificationResponse(notificationRepository.save(notification));
    }

    private void save(ParkingUser recipient, String title, String message, NotificationType type) {
        Notification notification = Notification.builder()
                .tenant(recipient.getTenant())
                .recipient(recipient)
                .title(title)
                .message(message)
                .type(type)
                .read(false)
                .build();
        notificationRepository.save(notification);
    }
}
