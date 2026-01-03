/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pet.businessdomain.notificationservice.dto;

import jakarta.persistence.Transient;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 *
 * @author Pc
 */
@Data
public class NotificationDto {
    private Long id;
    private Long uid; // ID del usuario propietario de la cuenta
    private UserDto userDto; // ID del usuario propietario de la cuenta
    private Long aid; // ID del usuario propietario de la cuenta
    private PetDto petDto; // ID del usuario propietario de la cuenta
    private Long otherid;
    private UserDto otherUserDto;
    private String type; // ID del animal propietario de la cuenta
    private String status;
    private String title;
    private String body;
    private PetDto pet;
    private boolean isRead;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime aceptAt = LocalDateTime.now();

    
}
