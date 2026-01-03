/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pet.businessdomain.requestservice.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 *
 * @author Pc
 */
@Data
public class PetDto {
    private Long id;
    private Long uid; // ID del usuario propietario de la cuenta
    private String name;
    private String specie;
    private String breed;
    private Integer age;
    private String sex;
    private String size;
    private String color;
    private String description;
    private String address;
    private Boolean vaccinated;
    private Boolean sterilized;
    private String status; // "En adopción", "Adoptado", "Reservado"
    private LocalDateTime adoptionAt;
    private List<String> images;
    private Integer favorites;
    private Double distance;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<?> userdto;
    private List<String> questions;
    private Boolean goodWithDogs;
    private Boolean goodWithCats;
    private Boolean goodWithKids;
    private Boolean hasMicrochip;
    private Boolean hasDesparasite;
    private String energyLevel; // LOW, MEDIUM, HIGH

}
