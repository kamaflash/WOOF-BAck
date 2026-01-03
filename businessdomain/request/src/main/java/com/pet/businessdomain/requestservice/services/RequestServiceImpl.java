/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pet.businessdomain.requestservice.services;


import com.pet.businessdomain.requestservice.common.BusinessTransactions;
import com.pet.businessdomain.requestservice.dto.NotificationDto;
import com.pet.businessdomain.requestservice.dto.RequestDto;
import com.pet.businessdomain.requestservice.entities.Request;
import com.pet.businessdomain.requestservice.exceptions.BusinessRuleException;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import com.pet.businessdomain.requestservice.mapper.RequestMapper;
import com.pet.businessdomain.requestservice.repository.RequestRepository;

/**
 *
 * @author Pc
 */
@Service
@Slf4j
public class RequestServiceImpl implements IRequestService {

    @Autowired
    private RequestRepository requestRepository;
    @Autowired
    private RequestMapper requestMapper;
    // Add any required dependencies here (e.g., repositories, mappers)
    @Autowired
    private BusinessTransactions businessTransactions;
    @Override
    public List<RequestDto> getAllRequests() {
        // Implementation here
        return null;
    }

    @Override
    public RequestDto getRequestById(Long id) {
        // Implementation here
        return null;
    }

    @Override
    public RequestDto createRequest(RequestDto requestDto) {
        // Implementation here
        return null;
    }

    @Override
    public RequestDto updateRequest(Long id, RequestDto requestDto) throws BusinessRuleException {
        // Implementation here
        Optional<Request> opt = requestRepository.findById(id);
        log.info("resRequest:::::::" + opt.get());
        Request resRequest = requestMapper.toOptional(opt);
        if (resRequest != null) {
            resRequest.setUserId(requestDto.getUserId());      // 🧍 ID del usuario que solicita
            resRequest.setPetId(requestDto.getPetId());        // 🐶 ID del animal
            resRequest.setStatus(requestDto.getStatus());      // 🔄 Estado de la solicitud
            resRequest.setMessage(requestDto.getMessage());    // 💬 Mensaje del adoptante
            resRequest.setAnswers(requestDto.getAnswers());    // 📋 Respuestas al cuestionario
            resRequest.setNotes(requestDto.getNotes());        // 🗒️ Notas opcionales
            resRequest.setUpdateAt(LocalDateTime.now());
            resRequest.setQuestions(requestDto.getQuestions());

        } else {
            BusinessRuleException businessRuleException = new BusinessRuleException("0002", "Error validación. Transacion no localizada. ", HttpStatus.PRECONDITION_FAILED);
            throw businessRuleException;
        }
        log.info("resRequest:::::::" + resRequest);
        RequestDto save = requestMapper.toDto(requestRepository.save(resRequest));
        try {
            sendNotification(resRequest);
        } catch (Exception e) {
            // Loggear error pero no fallar la operación principal
            log.error("Error enviando notificación: {}", e.getMessage());
        }
        return save;
    }

    @Override
    public void deleteRequest(Long id) {
        // Implementation here
    }
    @Override
    public RequestDto getFull(Long id) throws BusinessRuleException  {
        // Implementation
        Optional<Request> opt = requestRepository.findById(id);
        Request request = null;
        if(!opt.isEmpty()) {
            request = opt.get();

        }
      //  List<?> products = businessTransactions.getProduct(id);

        if (request != null) {
            RequestDto dto = requestMapper.toDto(request);
       //     dto.setProducts(products);
            return dto;
        } else {
            BusinessRuleException businessRuleException = new BusinessRuleException("0002", "Error validación. Transacion no localizada. ", HttpStatus.PRECONDITION_FAILED);
            throw businessRuleException;
        }
    }
    @Override
    public RequestDto getUserByRequest(RequestDto request) throws BusinessRuleException  {
        // Implementation
        List<?> userList = businessTransactions.getUser(request.getUserId());
        List<?> userProList = businessTransactions.getUser(request.getProUid());
        List<?> petList = businessTransactions.getPet(request.getPetId(), request.getUserId());
        request.setUserdto(userList);
        request.setUserprodto(userProList);
        request.setPetdto(petList);
        if (request != null) {
            //     dto.setProducts(products);
            return request;
        } else {
            BusinessRuleException businessRuleException = new BusinessRuleException("0002", "Error validación. Transacion no localizada. ", HttpStatus.PRECONDITION_FAILED);
            throw businessRuleException;
        }
    }

    @Override
    public RequestDto getAnimalByRequest(RequestDto request) throws BusinessRuleException  {
        // Implementation
        List<?> petList = businessTransactions.getPet(request.getPetId(),request.getUserId());
        request.setPetdto(petList);

        if (request != null) {
            //     dto.setProducts(products);

            return request;
        } else {
            BusinessRuleException businessRuleException = new BusinessRuleException("0002", "Error validación. Transacion no localizada. ", HttpStatus.PRECONDITION_FAILED);
            throw businessRuleException;
        }
    }

    private void sendNotification(Request request) {
        // Ejecutar en un hilo separado para no bloquear
        CompletableFuture.runAsync(() -> {
            try {
                String title = "Actualización solicitud adopción " + request.getId();
                String body = "Nuevo estado: " + request.getStatus();

                NotificationDto notificationDto = new NotificationDto();
                notificationDto.setUid(request.getUserId());
                notificationDto.setOtherid(request.getProUid());
                notificationDto.setAid(request.getPetId());
                notificationDto.setStatus(request.getStatus());
                notificationDto.setTitle(title);
                notificationDto.setBody(body);

                log.info("Creando notificación: {}", notificationDto);

                // Llamada asíncrona
                businessTransactions.saveNotificationAsync(notificationDto)
                        .doOnSuccess(response -> log.info("Notificación enviada exitosamente: {}", response))
                        .doOnError(error -> log.error("Error enviando notificación: {}", error.getMessage()))
                        .subscribe(); // Suscribirse para ejecutar

            } catch (Exception e) {
                log.error("Error en sendNotification: {}", e.getMessage(), e);
            }
        });
    }
}
