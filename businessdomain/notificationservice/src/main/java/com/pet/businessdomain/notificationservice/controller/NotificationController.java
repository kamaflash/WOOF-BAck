/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pet.businessdomain.notificationservice.controller;


import com.pet.businessdomain.notificationservice.common.BusinessTransactions;
import com.pet.businessdomain.notificationservice.dto.LikeDto;
import com.pet.businessdomain.notificationservice.dto.NotificationDto;
import com.pet.businessdomain.notificationservice.dto.UserDto;
import com.pet.businessdomain.notificationservice.entities.Notification;
import com.pet.businessdomain.notificationservice.exceptions.BusinessRuleException;
import com.pet.businessdomain.notificationservice.services.ICloudinaryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import com.pet.businessdomain.notificationservice.mapper.NotificationMapper;
import com.pet.businessdomain.notificationservice.repository.NotificationRepository;
import com.pet.businessdomain.notificationservice.services.INotificationService;

/**
 *
 * @author Pc
 */
@Slf4j
@RestController
@RequestMapping("/api/notification")
public class NotificationController {

    @Autowired
    private INotificationService notificationService;
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private NotificationMapper notificationMapper;
    @Autowired
    private ICloudinaryService cloudinaryService;
    @Autowired
    private BusinessTransactions businessTransactions;

    // --------------------------------------------------------
    // GET: Obtener TODOS los notificationes
    // --------------------------------------------------------
    @GetMapping
    public ResponseEntity<?> getAllPMath() {
        List<NotificationDto> listNotificationDto = notificationMapper.toDtoList(notificationRepository.findAll());
        if (listNotificationDto.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No existen notification");
        } else {
            return ResponseEntity.ok(listNotificationDto);
        }
    }
    @GetMapping("/{uid}")
    public ResponseEntity<?> getAllPetsById(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @PathVariable(name = "uid") Long uid) {

        int size = (page == 0) ? 9 : 20;
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
        Page<NotificationDto> petsDtoPage = notificationService.getNotificationsByUidWithUserAndPet(uid, pageable);

        if (petsDtoPage.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No existen mascotas");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("notification", petsDtoPage.getContent());
        response.put("currentPage", petsDtoPage.getNumber());
        response.put("totalItems", petsDtoPage.getTotalElements());
        response.put("totalPages", petsDtoPage.getTotalPages());
        response.put("pageSize", petsDtoPage.getSize());
        response.put("hasNext", petsDtoPage.hasNext());
        response.put("hasPrevious", petsDtoPage.hasPrevious());

        return ResponseEntity.ok(response);
    }
    // --------------------------------------------------------
    // GET: Obtener notificationes por usuario
    // --------------------------------------------------------
    @GetMapping("/user/{uid}")
    public ResponseEntity<List<NotificationDto>> getByUser(@PathVariable(name = "uid")  Long uid) throws BusinessRuleException {
        List<NotificationDto> listNotificationDto = notificationService.getNotificationByUid(uid);
        if (listNotificationDto.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        } else {
            return ResponseEntity.ok(listNotificationDto);
        }
    }

    // --------------------------------------------------------
    // GET: Obtener notificationes por animal
    // --------------------------------------------------------
    @GetMapping("/animal/{aid}")
    public ResponseEntity<List<NotificationDto>> getByAnimal(@PathVariable(name = "aid")  Long aid) throws BusinessRuleException {
        List<NotificationDto> listNotificationDto = notificationService.getNotificationByAid(aid);
        if (listNotificationDto.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        } else {
            return ResponseEntity.ok(listNotificationDto);
        }
    }

    // --------------------------------------------------------
    // POST: Crear un notification (petición realizada)
    // --------------------------------------------------------
    @PostMapping
    public ResponseEntity<NotificationDto> createNotification(@RequestBody NotificationDto notificationDto) {
        // Convertir DTO a Entidad
        log.info("Creación notificacion ok.");

        Notification notification = notificationMapper.toEntity(notificationDto);
        notification.setCreatedAt(LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.CREATED).body(notificationMapper.toDto(notificationRepository.save(notification)));
    }

    // --------------------------------------------------------
    // PUT: Aceptar un notification
    // --------------------------------------------------------
    @PutMapping("/{id}/accept")
    public ResponseEntity<Notification> acceptNotification(@PathVariable Long id) throws BusinessRuleException {
        Notification updated = notificationService.acceptNotification(id);
        if (updated == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        } else {
            return ResponseEntity.ok(updated);
        }
    }

    // --------------------------------------------------------
    // PUT: Rechazar un notification
    // --------------------------------------------------------
    @PutMapping("/{id}/reject")
    public ResponseEntity<Notification> rejectNotification(@PathVariable Long id) throws BusinessRuleException {
        Notification updated = notificationService.rejectNotification(id);
        if (updated == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        } else {
            return ResponseEntity.ok(updated);
        }
    }







    @GetMapping("/uid/{uid}")
    public ResponseEntity<?> getNotificationByUid(@PathVariable(name = "uid") Long uid) throws BusinessRuleException {
        List<NotificationDto> list = notificationService.getNotificationByUid(uid);
        if (!list.isEmpty()) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(list);
        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No existe la obra");
        }
    }

    @GetMapping("/user/{uid}/pet/{aid}")
    public ResponseEntity<?> getNotificationByUidAndAid(@PathVariable(name = "uid") Long uid, @PathVariable(name = "aid") Long aid) throws BusinessRuleException {
        Optional<Notification> opt = notificationRepository.getNotificationByUidAndAid(uid,aid);
        Notification notification = notificationMapper.toOptional(opt);
        NotificationDto notificationDto = notificationService.getUserByPet(notification);
        if (opt.isPresent() && notificationDto != null) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(notificationDto);
        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No existe la obra");
        }
    }

    @GetMapping("/notification/full")
    public ResponseEntity<?> get(@RequestParam(name = "uid") Long uid) {
        List<Notification> notifications = notificationRepository.findByUid(uid);
        List<NotificationDto> notificationsList = notificationMapper.toDtoList(notifications);

        if (notificationsList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).build();
        } else {
            return ResponseEntity.ok(notificationsList);
        }
    }

    @DeleteMapping("/all")
    public ResponseEntity<?> deletePetAll() {
            notificationRepository.deleteAll();
            return ResponseEntity.status(HttpStatus.ACCEPTED).body("Hecho");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePet(@PathVariable(name = "id") Long id) {
        Optional<Notification> find = notificationRepository.findById(id);
        if (find.isPresent()) {
            NotificationDto notificationDto = notificationMapper.toDto(find.get());
            notificationRepository.delete(find.get());
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(notificationDto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("No es aceptable");
        }
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePet(@PathVariable(name="id") Long id, @RequestBody NotificationDto notificationDto) throws BusinessRuleException {
        if (notificationDto != null) {
            NotificationDto dto = notificationService.updatePet(id, notificationDto);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(dto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("No es aceptable");
        }
    }

    private boolean likeAnimal( LikeDto request) {


        return notificationService.checkNotification(request.getUid(), request.getAid());
    }

}
