/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pet.businessdomain.matchservice.services;


import com.pet.businessdomain.matchservice.common.BusinessTransactions;
import com.pet.businessdomain.matchservice.dto.MatchDto;
import com.pet.businessdomain.matchservice.dto.PetDto;
import com.pet.businessdomain.matchservice.dto.UserDto;
import com.pet.businessdomain.matchservice.entities.Match;
import com.pet.businessdomain.matchservice.exceptions.BusinessRuleException;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.pet.businessdomain.matchservice.mapper.MatchMapper;
import com.pet.businessdomain.matchservice.repository.MatchRepository;

/**
 *
 * @author Pc
 */
@Service
@Slf4j
public class MatchServiceImpl implements IMatchService {

    @Autowired
    private MatchRepository matchRepository;
    @Autowired
    private MatchMapper matchMapper;
    // Add any required dependencies here (e.g., repositories, mappers)
    @Autowired
    private BusinessTransactions businessTransactions;


    @Override
    public MatchDto updatePet(Long id, MatchDto matchDto) throws BusinessRuleException {
        // Implementation here
        Optional<Match> opt = matchRepository.findById(id);
        Match resPet = matchMapper.toOptional(opt);
        if (resPet != null) {
            resPet.setStatus(matchDto.getStatus());
            resPet.setAceptAt(matchDto.getAceptAt());
        } else {
            BusinessRuleException businessRuleException = new BusinessRuleException("0002", "Error validación. Transacion no localizada. ", HttpStatus.PRECONDITION_FAILED);
            throw businessRuleException;
        }
        MatchDto save = matchMapper.toDto(matchRepository.save(resPet));
        return save;
    }

    @Override
    public MatchDto getUserByPet(Match match) throws BusinessRuleException  {
        // Implementation
        List<?> userList = businessTransactions.getUser(match.getUid());
        MatchDto matchDto = matchMapper.toDto(match);
        //matchDto.setUserdto(userList);
        if (matchDto != null) {
            //     dto.setProducts(products);
            return matchDto;
        } else {
            BusinessRuleException businessRuleException = new BusinessRuleException("0002", "Error validación. Transacion no localizada. ", HttpStatus.PRECONDITION_FAILED);
            throw businessRuleException;
        }
    }
    @Override
    public List<MatchDto> getMatchByUid(Long uid) throws BusinessRuleException  {
        List<Match> matchList = matchRepository.findByUid(uid);
        List<MatchDto> matchDtoList = matchMapper.toDtoList(matchList);

        if (!matchDtoList.isEmpty()) {
            // Añadir parámetro adicional a cada MatchDto
            return matchDtoList.stream()
                    .map(matchDto -> {
                        // Añadir el parámetro que necesites
                        matchDto.setPet(businessTransactions.getPet(matchDto.getAid(), uid));
                        return matchDto;
                    })
                    .collect(Collectors.toList());
        } else {
            BusinessRuleException businessRuleException = new BusinessRuleException("0002", "Error validación. Transacion no localizada. ", HttpStatus.PRECONDITION_FAILED);
            throw businessRuleException;
        }
    }
    @Override
    public List<MatchDto> getMatchByAid(Long aid) throws BusinessRuleException  {
        // Implementation
        List<Match> matchList = matchRepository.findByAid(aid);
        List<MatchDto> matchDto = matchMapper.toDtoList(matchList);
        //matchDto.setUserdto(userList);
        if (matchDto != null) {
            //     dto.setProducts(products);
            return matchDto;
        } else {
            BusinessRuleException businessRuleException = new BusinessRuleException("0002", "Error validación. Transacion no localizada. ", HttpStatus.PRECONDITION_FAILED);
            throw businessRuleException;
        }
    }

    public Match acceptMatch(Long id) throws BusinessRuleException {
        Match m = matchRepository.findById(id).orElseThrow();
        if (m != null) {
            m.setStatus("Adeptado");
            m.setAceptAt(LocalDateTime.now());
            return matchRepository.save(m);

        } else {
            BusinessRuleException businessRuleException = new BusinessRuleException("0002", "Error validación. Transacion no localizada. ", HttpStatus.PRECONDITION_FAILED);
            throw businessRuleException;
        }
    }

    public Match rejectMatch(Long id) throws BusinessRuleException {
        Match m = matchRepository.findById(id).orElseThrow();
        if (m != null) {
            m.setStatus("Rechazado");
            m.setAceptAt(LocalDateTime.now());
            return matchRepository.save(m);

        } else {
            BusinessRuleException businessRuleException = new BusinessRuleException("0002", "Error validación. Transacion no localizada. ", HttpStatus.PRECONDITION_FAILED);
            throw businessRuleException;
        }
    }

    public boolean checkMatch(Long uid, Long aid) {

        UserDto user = businessTransactions.getUser(uid).get(0);
        PetDto pet = businessTransactions.getPet(aid, uid);

        int puntos = 0;

        // -----------------------------------------
        // ❌ REGLAS DE INCOMPATIBILIDAD DIRECTA
        // -----------------------------------------

        // Si tiene hijos y la mascota no es apta
        if (Boolean.TRUE.equals(user.getHasKids()) &&
                Boolean.FALSE.equals(pet.getGoodWithKids())) {
            return false;
        }

        // Si tiene perros y el pet no es compatible
        if (Boolean.TRUE.equals(user.getHasOtherDogs()) &&
                Boolean.FALSE.equals(pet.getGoodWithDogs())) {
            return false;
        }

        // Si tiene gatos y el pet no es compatible
        if (Boolean.TRUE.equals(user.getHasOtherCats()) &&
                Boolean.FALSE.equals(pet.getGoodWithCats())) {
            return false;
        }

        // Vivienda demasiado pequeña para mascota grande
        if ("large".equalsIgnoreCase(pet.getSize()) &&
                "APARTMENT_SMALL".equalsIgnoreCase(user.getHouseType())) {
            return false;
        }

        // -----------------------------------------
        // ✔ SISTEMA DE PUNTOS POSITIVO
        // -----------------------------------------

        // Buena compatibilidad con niños
        if (Boolean.TRUE.equals(user.getHasKids()) &&
                Boolean.TRUE.equals(pet.getGoodWithKids())) {
            puntos += 2;
        }

        // Buena compatibilidad con perros del usuario
        if (Boolean.TRUE.equals(user.getHasOtherDogs()) &&
                Boolean.TRUE.equals(pet.getGoodWithDogs())) {
            puntos += 2;
        }

        // Buena compatibilidad con gatos
        if (Boolean.TRUE.equals(user.getHasOtherCats()) &&
                Boolean.TRUE.equals(pet.getGoodWithCats())) {
            puntos += 2;
        }

        // Jardín y mascota grande
        if ("Grande".equalsIgnoreCase(pet.getSize()) &&
                Boolean.TRUE.equals(user.getHasGarden())) {
            puntos += 3;
        }

        // Experiencia del usuario con mascotas
        if (pet.getGoodWithDogs() == Boolean.FALSE ||
                pet.getGoodWithCats() == Boolean.FALSE) {

            if ("true".equalsIgnoreCase(user.getExperienceWithPets())) {
                puntos += 2;
            } else {
                puntos -= 2;
            }
        }

        // Edad y tiempo disponible
        if (pet.getAge() != null && pet.getAge() < 3) {
            if ("Alto".equalsIgnoreCase(user.getTimeAtHome())) {
                puntos -= 2; // necesita tiempo
            } else {
                puntos += 1;
            }
        }

        // Mascota esterilizada suma puntos
        if (Boolean.TRUE.equals(pet.getSterilized())) {
            puntos += 1;
        }

        // -----------------------------------------
        // ✔ DECISIÓN FINAL
        // -----------------------------------------

        return puntos >= 4;
    }

}
