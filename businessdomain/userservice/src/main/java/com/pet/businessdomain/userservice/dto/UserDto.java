/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pet.businessdomain.userservice.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Transient;
import lombok.Data;

/**
 *
 * @author Pc
 */
@Data
public class UserDto {

    private Long id;
    private String username;
    private String email;
    private String password;
    private String name;
    private String lastname;
    private String bio;
    private String accountType;
    private String cif;
    private String phone;
    private String address;
    private String website;
    private String avatarUrl;

    // Campos de hogar (usuarios)
    private String houseType;
    private Boolean hasGarden = false;
    private Boolean hasKids = false;
    private Boolean hasOtherPets = false;
    private Boolean hasOtherDogs = false;
    private Boolean hasOtherCats = false;

    // Campos para match
    private String experienceWithPets;
    private String activityLevel;
    private String timeAtHome;
    private String budgetLevel;
    private Boolean allowsLargePets = true;

    // Campos de protectora
    private Boolean hasVeterinarian = false;
    private Boolean isOpen24h = false;
    private Boolean acceptsVolunteers = false;
    private Boolean acceptsDonations = false;
    private String services;
    private String facilities;
    private Integer capacity;
    private Integer currentAnimals;
    private String adoptionProcess;
    private String openingHours;
    private List<?> pet;
    private Double feedback;
    private Double distance;
    private Integer totalAnimals;
    private Integer adoptAnimals;

    private boolean enabled = true;


}
