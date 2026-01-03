/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pet.businessdomain.notificationservice.services;


import com.pet.businessdomain.notificationservice.common.BusinessTransactions;
import com.pet.businessdomain.notificationservice.dto.NotificationDto;
import com.pet.businessdomain.notificationservice.dto.PetDto;
import com.pet.businessdomain.notificationservice.dto.UserDto;
import com.pet.businessdomain.notificationservice.entities.Notification;
import com.pet.businessdomain.notificationservice.exceptions.BusinessRuleException;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.pet.businessdomain.notificationservice.mapper.NotificationMapper;
import com.pet.businessdomain.notificationservice.repository.NotificationRepository;

/**
 *
 * @author Pc
 */
@Service
@Slf4j
public class NotificationServiceImpl implements INotificationService {

    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private NotificationMapper notificationMapper;
    // Add any required dependencies here (e.g., repositories, mappers)
    @Autowired
    private BusinessTransactions businessTransactions;


    @Override
    public NotificationDto updatePet(Long id, NotificationDto notificationDto) throws BusinessRuleException {
        // Implementation here
        Optional<Notification> opt = notificationRepository.findById(id);
        Notification resPet = notificationMapper.toOptional(opt);
        if (resPet != null) {
            resPet.setRead(notificationDto.isRead());
        } else {
            BusinessRuleException businessRuleException = new BusinessRuleException("0002", "Error validación. Transacion no localizada. ", HttpStatus.PRECONDITION_FAILED);
            throw businessRuleException;
        }
        NotificationDto save = notificationMapper.toDto(notificationRepository.save(resPet));
        return save;
    }

    @Override
    public NotificationDto getUserByPet(Notification notification) throws BusinessRuleException  {
        // Implementation
        List<?> userList = businessTransactions.getUser(notification.getUid());
        NotificationDto notificationDto = notificationMapper.toDto(notification);
        //notificationDto.setUserdto(userList);
        if (notificationDto != null) {
            //     dto.setProducts(products);
            return notificationDto;
        } else {
            BusinessRuleException businessRuleException = new BusinessRuleException("0002", "Error validación. Transacion no localizada. ", HttpStatus.PRECONDITION_FAILED);
            throw businessRuleException;
        }
    }
    @Override
    public List<NotificationDto> getNotificationByUid(Long uid) throws BusinessRuleException  {
        List<Notification> notificationList = notificationRepository.findByUid(uid);
        List<NotificationDto> notificationDtoList = notificationMapper.toDtoList(notificationList);

        if (!notificationDtoList.isEmpty()) {
            // Añadir parámetro adicional a cada NotificationDto
            return notificationDtoList.stream()
                    .map(notificationDto -> {
                        // Añadir el parámetro que necesites
                        notificationDto.setPet(businessTransactions.getPet(notificationDto.getAid(), uid));
                        return notificationDto;
                    })
                    .collect(Collectors.toList());
        } else {
            BusinessRuleException businessRuleException = new BusinessRuleException("0002", "Error validación. Transacion no localizada. ", HttpStatus.PRECONDITION_FAILED);
            throw businessRuleException;
        }
    }
    @Override
    public List<NotificationDto> getNotificationByAid(Long aid) throws BusinessRuleException  {
        // Implementation
        List<Notification> notificationList = notificationRepository.findByAid(aid);
        List<NotificationDto> notificationDto = notificationMapper.toDtoList(notificationList);
        //notificationDto.setUserdto(userList);
        if (notificationDto != null) {
            //     dto.setProducts(products);
            return notificationDto;
        } else {
            BusinessRuleException businessRuleException = new BusinessRuleException("0002", "Error validación. Transacion no localizada. ", HttpStatus.PRECONDITION_FAILED);
            throw businessRuleException;
        }
    }

    public Notification acceptNotification(Long id) throws BusinessRuleException {
        Notification m = notificationRepository.findById(id).orElseThrow();
        if (m != null) {
            m.setStatus("Adeptado");
            m.setAceptAt(LocalDateTime.now());
            return notificationRepository.save(m);

        } else {
            BusinessRuleException businessRuleException = new BusinessRuleException("0002", "Error validación. Transacion no localizada. ", HttpStatus.PRECONDITION_FAILED);
            throw businessRuleException;
        }
    }

    public Notification rejectNotification(Long id) throws BusinessRuleException {
        Notification m = notificationRepository.findById(id).orElseThrow();
        if (m != null) {
            m.setStatus("Rechazado");
            m.setAceptAt(LocalDateTime.now());
            return notificationRepository.save(m);

        } else {
            BusinessRuleException businessRuleException = new BusinessRuleException("0002", "Error validación. Transacion no localizada. ", HttpStatus.PRECONDITION_FAILED);
            throw businessRuleException;
        }
    }

    public boolean checkNotification(Long uid, Long aid) {

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
    @Override
    public Page<NotificationDto> getNotificationsByUidWithUserAndPet(Long uid, Pageable pageable) {
        Page<Notification> notifications = notificationRepository.findByUid(uid, pageable);

        return notifications.map(notification -> {
            NotificationDto dto = notificationMapper.toDto(notification);

            // Obtener y setear userDto
            List<UserDto> userDto = businessTransactions.getUser(notification.getUid());
            dto.setUserDto(userDto.get(0));
// Obtener y setear userDto
            List<UserDto> userOtherDto = businessTransactions.getUser(notification.getOtherid());
            dto.setOtherUserDto(userOtherDto.get(0));
            // Obtener y setear petDto usando el aid de la notificación
            PetDto petDto = businessTransactions.getPet(notification.getAid(), 0L); // Ajusta el método según tu implementación
            log.info("ANIMAL PETS!!!!!!!!!!!!!!!"+petDto);
            dto.setPetDto(petDto);

            return dto;
        });
    }
}
