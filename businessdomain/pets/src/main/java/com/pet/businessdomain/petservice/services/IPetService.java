/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.pet.businessdomain.petservice.services;

import com.pet.businessdomain.petservice.dto.PetDto;
import com.pet.businessdomain.petservice.entities.Pet;
import com.pet.businessdomain.petservice.exceptions.BusinessRuleException;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 *
 * @author Pc
 */

public interface IPetService {
    List<PetDto> getAllPets();
    PetDto getPetById(Long id);
    PetDto getUserByPet(Pet pet) throws BusinessRuleException;
    PetDto createPet(PetDto petDto);
    PetDto updatePet(Long id, PetDto petDto) throws BusinessRuleException;
    void deletePet(Long id);
    PetDto getFull(Long id) throws BusinessRuleException;
    List<?> getMatchByUid(Long uid);
    List<PetDto> setDistanceToPet(Page<PetDto> petsDtoPage, Long uid, Page<Pet> petsPage );

}
