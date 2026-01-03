/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pet.businessdomain.matchservice.entities;

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
@Table(name = "matchs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long uid; // ID del usuario propietario de la cuenta
    private Long aid; // ID del animal propietario de la cuenta
    private String status;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime aceptAt = LocalDateTime.now();

}

