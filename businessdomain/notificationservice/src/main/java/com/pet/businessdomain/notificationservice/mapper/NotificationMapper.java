/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.pet.businessdomain.notificationservice.mapper;

import com.pet.businessdomain.notificationservice.dto.NotificationDto;
import com.pet.businessdomain.notificationservice.entities.Notification;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Optional;

/**
 *
 * @author Pc
 */
@Mapper(componentModel = "spring")
public interface NotificationMapper {
    NotificationDto toDto(Notification notification);
    Notification toEntity(NotificationDto notificationDto);
    List<NotificationDto> toDtoList(List<Notification> notifications);
    default Notification toOptional(Optional<Notification> opt) {
        return opt.orElse(null); // o lanzar excepción si prefieres
    }
}
