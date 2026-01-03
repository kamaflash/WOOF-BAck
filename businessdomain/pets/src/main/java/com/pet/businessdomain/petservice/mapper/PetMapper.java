/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.pet.businessdomain.petservice.mapper;

import com.pet.businessdomain.petservice.dto.PetDto;
import com.pet.businessdomain.petservice.entities.Pet;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Optional;

/**
 *
 * @author Pc
 */
@Mapper(componentModel = "spring")
public interface PetMapper {
    PetDto toDto(Pet pet);
    Pet toEntity(PetDto petDto);
    List<PetDto> toDtoList(List<Pet> pets);
    default Pet toOptional(Optional<Pet> opt) {
        return opt.orElse(null); // o lanzar excepción si prefieres
    }
}
