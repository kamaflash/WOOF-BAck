/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pet.businessdomain.petservice.services;


import com.pet.businessdomain.petservice.common.BusinessTransactions;
import com.pet.businessdomain.petservice.dto.PetDto;
import com.pet.businessdomain.petservice.dto.UserDto;
import com.pet.businessdomain.petservice.entities.Pet;
import com.pet.businessdomain.petservice.exceptions.BusinessRuleException;
import com.pet.businessdomain.petservice.mapper.PetMapper;
import com.pet.businessdomain.petservice.repository.PetRepository;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 *
 * @author Pc
 */
@Service
@Slf4j
public class PetServiceImpl implements IPetService {

    @Autowired
    private PetRepository petRepository;
    @Autowired
    private PetMapper petMapper;
    // Add any required dependencies here (e.g., repositories, mappers)
    @Autowired
    private BusinessTransactions businessTransactions;
    @Autowired
    private ILocationService locationService;
    @Override
    public List<PetDto> getAllPets() {
        // Implementation here
        return null;
    }

    @Override
    public PetDto getPetById(Long id) {
        // Implementation here
        return null;
    }
    @Override
    public List<?> getMatchByUid(Long uid) {
        List<?> matchList = businessTransactions.getMatch(uid);
        return matchList;
    }

    @Override
    public PetDto createPet(PetDto petDto) {
        // Implementation here
        return null;
    }

    @Override
    public PetDto updatePet(Long id, PetDto petDto) throws BusinessRuleException {
        // Implementation here
        Optional<Pet> opt = petRepository.findById(id);
        log.info("resPet:::::::" + opt.get());
        Pet resPet = petMapper.toOptional(opt);
        if (resPet != null) {
            resPet.setUid(petDto.getUid());
            resPet.setName(petDto.getName());
            resPet.setSpecie(petDto.getSpecie());
            resPet.setBreed(petDto.getBreed());
            resPet.setAge(petDto.getAge());
            resPet.setSex(petDto.getSex());
            resPet.setSize(petDto.getSize());
            resPet.setColor(petDto.getColor());
            resPet.setDescription(petDto.getDescription());
            resPet.setAddress(petDto.getAddress());
            resPet.setVaccinated(petDto.getVaccinated());
            resPet.setSterilized(petDto.getSterilized());
            resPet.setStatus(petDto.getStatus());
            resPet.setAdoptionAt(petDto.getAdoptionAt());
            resPet.setImages(petDto.getImages());
            resPet.setFavorites(petDto.getFavorites());
            resPet.setUpdatedAt(LocalDateTime.now());
            resPet.setQuestions(petDto.getQuestions());
            resPet.setHasDesparasite(petDto.getHasDesparasite());
            resPet.setSterilized(petDto.getSterilized());
            resPet.setGoodWithCats(petDto.getGoodWithCats());
            resPet.setGoodWithDogs(petDto.getGoodWithDogs());
            resPet.setGoodWithKids(petDto.getGoodWithKids());
            resPet.setHasMicrochip(petDto.getHasMicrochip());
        } else {
            BusinessRuleException businessRuleException = new BusinessRuleException("0002", "Error validación. Transacion no localizada. ", HttpStatus.PRECONDITION_FAILED);
            throw businessRuleException;
        }
        log.info("resPet:::::::" + resPet);
        PetDto save = petMapper.toDto(petRepository.save(resPet));
        return save;
    }

    @Override
    public void deletePet(Long id) {
        // Implementation here
    }
    @Override
    public PetDto getFull(Long id) throws BusinessRuleException  {
        // Implementation
        Optional<Pet> opt = petRepository.findById(id);
        Pet pet = null;
        if(!opt.isEmpty()) {
            pet = opt.get();

        }
      //  List<?> products = businessTransactions.getProduct(id);

        if (pet != null) {
            PetDto dto = petMapper.toDto(pet);
       //     dto.setProducts(products);
            return dto;
        } else {
            BusinessRuleException businessRuleException = new BusinessRuleException("0002", "Error validación. Transacion no localizada. ", HttpStatus.PRECONDITION_FAILED);
            throw businessRuleException;
        }
    }

    @Override
    public PetDto getUserByPet(Pet pet) throws BusinessRuleException  {
        // Implementation
        List<?> userList = businessTransactions.getPetWitchUser(pet.getUid());
        PetDto petDto = petMapper.toDto(pet);
        petDto.setUserdto(userList);
        if (petDto != null) {
            //     dto.setProducts(products);
            return petDto;
        } else {
            BusinessRuleException businessRuleException = new BusinessRuleException("0002", "Error validación. Transacion no localizada. ", HttpStatus.PRECONDITION_FAILED);
            throw businessRuleException;
        }
    }

    @Override
    public List<PetDto> setDistanceToPet(Page<PetDto> petsDtoPage, Long uid, Page<Pet> petsPage ) {
        UserDto userDto = businessTransactions.getUser(uid);
        String userAddress = userDto.getAddress();

        List<PetDto> petsList = petsDtoPage.getContent();

        for (int i = 0; i < petsList.size(); i++) {
            PetDto petDto = petsList.get(i);
            Pet pet = petsPage.getContent().get(i);

            double distanceKm = locationService
                    .distanceBetweenAddresses(userAddress, pet.getAddress());

            petDto.setDistance(distanceKm);
        }

        return petsDtoPage.getContent();
    }
}
