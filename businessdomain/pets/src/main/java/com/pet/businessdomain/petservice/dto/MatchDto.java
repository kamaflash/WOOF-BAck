/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pet.businessdomain.petservice.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 *
 * @author Pc
 */
@Data
public class MatchDto {
    private Long id;
    private Long uid; // ID del usuario propietario de la cuenta
    private Long aid; // ID del animal propietario de la cuenta
    private String status;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime aceptAt = LocalDateTime.now();

    
}
