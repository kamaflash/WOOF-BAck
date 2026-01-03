/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pet.businessdomain.notificationservice.entities;

import com.pet.businessdomain.notificationservice.dto.PetDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 *
 * @author Pc
 */
@Entity
@Table(name = "notification")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long uid; // ID del usuario propietario de la cuenta
    private Long aid; // ID del usuario propietario de la cuenta
    private Long otherid;
    private String type; // ID del animal propietario de la cuenta
    private String status;
    private String title;
    private String body;
    private boolean isRead = false;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime aceptAt = LocalDateTime.now();

}

