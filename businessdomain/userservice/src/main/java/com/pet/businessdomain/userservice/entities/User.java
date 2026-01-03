/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pet.businessdomain.userservice.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

/**
 *
 * @author Pc
 */
@Entity
@Data
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String name;
    private String lastname;

    private String bio;
    private String accountType;
    private String cif;
    private String avatarUrl; // URL de Cloudinary

    // Datos de contacto
    private String phone;
    private String address;
    private String website;

    // Información sobre la vivienda y entorno (principalmente para usuarios)
    private String houseType;  // piso, chalet, etc.
    private Boolean hasGarden = false;
    private Boolean hasKids = false;
    private Boolean hasOtherPets = false;

    private boolean enabled = true;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    private String role = "USER_ROLE";

    // ----------------------------------------
    // CAMPOS PARA MATCH AUTOMÁTICO (NUEVOS)
    // ----------------------------------------

    // Diferenciar tipos de mascotas que tiene (más preciso que "hasOtherPets")
    private Boolean hasOtherDogs = false;
    private Boolean hasOtherCats = false;

    // Nivel de experiencia con animales
    // NONE, BASIC, EXPERIENCED
    private String experienceWithPets;

    // Actividad física del usuario
    // LOW, MEDIUM, HIGH
    private String activityLevel;

    // Tiempo que pasa en casa
    // LOW, MEDIUM, HIGH
    private String timeAtHome;

    // Capacidad económica
    // LOW, MEDIUM, HIGH
    private String budgetLevel;

    // Restricciones de vivienda o comunidad
    private Boolean allowsLargePets = true;

    // ----------------------------------------
    // CAMPOS ESPECÍFICOS PARA PROTECTORAS
    // ----------------------------------------

    // Servicios de la protectora
    private Boolean hasVeterinarian = false;          // ¿Tiene veterinario propio o de referencia?
    private Boolean isOpen24h = false;                // ¿Atención 24 horas?
    private Boolean acceptsVolunteers = false;        // ¿Acepta voluntarios?
    private Boolean acceptsDonations = false;         // ¿Acepta donaciones?

    // Información adicional de protectoras
    private String services;                          // Descripción de servicios ofrecidos
    private String facilities;                        // Descripción de instalaciones
    private Integer capacity;                         // Capacidad máxima de animales
    private Integer currentAnimals;                   // Número actual de animales acogidos
    private String adoptionProcess;                   // Descripción del proceso de adopción
    private String openingHours;                      // Horario de atención

    // ----------------------------------------
    // CAMPOS TRANSITORIOS: NO SE GUARDA EN BBDD
    // ----------------------------------------
    @Transient
    private List<?> pet;
    @Transient
    private Double feedback;

    // Getters, Setters, Constructors

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}