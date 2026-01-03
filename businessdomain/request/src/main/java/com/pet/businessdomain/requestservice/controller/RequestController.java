/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pet.businessdomain.requestservice.controller;


import com.pet.businessdomain.requestservice.dto.RequestDto;
import com.pet.businessdomain.requestservice.entities.Request;
import com.pet.businessdomain.requestservice.exceptions.BusinessRuleException;
import com.pet.businessdomain.requestservice.services.ICloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import com.pet.businessdomain.requestservice.mapper.RequestMapper;
import com.pet.businessdomain.requestservice.repository.RequestRepository;
import com.pet.businessdomain.requestservice.services.IRequestService;

/**
 *
 * @author Pc
 */
@RestController
@RequestMapping("/api/request")
public class RequestController {

    @Autowired
    private IRequestService requestService;
    @Autowired
    private RequestRepository requestRepository;
    @Autowired
    private RequestMapper requestMapper;
    @Autowired
    private ICloudinaryService cloudinaryService;

    @GetMapping
    public ResponseEntity<?> getAllRequests() {
        List<RequestDto> listRequestDto = requestMapper.toDtoList(requestRepository.findAll()).stream()
                .map(requestDto -> {
                    try {
                        return requestService.getUserByRequest(requestDto);
                    } catch (BusinessRuleException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();;

        if (listRequestDto.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No existen obras");
        } else {
            return ResponseEntity.ok(listRequestDto);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRequestById(@PathVariable(name = "id") Long id) throws BusinessRuleException {
        
        Optional<Request> opt = requestRepository.findById(id);
        Request request = requestMapper.toOptional(opt);
        RequestDto requestDto = requestService.getUserByRequest(requestMapper.toDto(request));

        if (opt.isPresent() && requestDto != null) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(requestDto);
        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No existe la obra");
        }
    }
//Comrpueba si tiene solicutid enviada
    @GetMapping("/exits/{userId}/{petId}")
    public ResponseEntity<?> get(@PathVariable(name = "userId") Long userId, @PathVariable(name = "petId") Long petId) throws BusinessRuleException {
        List<Request> requests = requestRepository.findByUserIdAndPetId(userId, petId);
        if(!requests.isEmpty()) {
            RequestDto request = requestMapper.toDto(requests.getFirst());
            RequestDto requestDto = requestService.getUserByRequest(request);
            if (requestDto== null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            } else {
                return ResponseEntity.ok(requestDto);
            }
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    //Trae las solicitudes por animal
    @GetMapping("/petId/{id}")
    public ResponseEntity<?> getFull(@PathVariable(name = "id") Long id) throws BusinessRuleException {
        List<Request> listSave = requestRepository.findByPetId(id);

        // Mapeamos cada RequestDto con el que devuelve el servicio (ya con usuario)
        List<RequestDto> listSaveDto = requestMapper.toDtoList(listSave).stream()
                .map(requestDto -> {
                    try {
                        return requestService.getUserByRequest(requestDto);
                    } catch (BusinessRuleException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();

        if (listSaveDto.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(listSaveDto);
        }
    }
    // Trae la solicitudes por usuario solicitante
    @GetMapping("/userId/{id}")
    public ResponseEntity<?> get(@PathVariable(name = "id") Long id) throws BusinessRuleException {
        List<Request> listSave = requestRepository.findByUserId(id);

        // Mapeamos cada RequestDto con el que devuelve el servicio (ya con usuario)
        List<RequestDto> listSaveDto = requestMapper.toDtoList(listSave).stream()
                .map(requestDto -> {
                    try {
                        return requestService.getUserByRequest(requestDto);
                    } catch (BusinessRuleException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();

        if (listSaveDto.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(listSaveDto);
        }
    }

    // Trae la solicitudes por usuario solicitante
    @GetMapping("/proUid/{uid}")
    public ResponseEntity<?> getProUid(
            @RequestParam(name = "page",defaultValue = "0") int page,
            @RequestParam(name = "status",defaultValue = "all") String status,
            @RequestParam(name = "search",defaultValue = "all") String search,
            @PathVariable(name = "uid") Long uid) {

        Pageable pageable = PageRequest.of(page, 8);

        try {
            Page<Request> requestsPage = requestRepository.findByProUidOrUserId(uid, pageable);
            if(!status.equals("all") && search.equals("all")){
                requestsPage = requestRepository.findByProUidOrUserIdAndStatus(uid,status,pageable);
            }

            if (requestsPage.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body("No existen peticiones para este usuario");
            }

            // Transformar a DTOs manejando la excepción
            List<RequestDto> dtos = requestsPage.getContent().stream()
                    .map(requestMapper::toDto)
                    .map(request -> {
                        try {
                            return requestService.getUserByRequest(request);
                        } catch (BusinessRuleException e) {

                            return request;
                        }
                    })
                    .filter(Objects::nonNull) // Filtrar nulos si decides devolver null
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("request", dtos);
            response.put("totalItems", requestsPage.getTotalElements());
            response.put("totalPages", requestsPage.getTotalPages());
            response.put("currentPage", page);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al procesar las peticiones: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<RequestDto> createRequest(@RequestBody RequestDto requestDto) {
        // Convertir DTO a Entidad
        Request request = requestMapper.toEntity(requestDto);
        request.setCreatedAt(LocalDateTime.now());
        request.setUpdateAt(LocalDateTime.now());
        // Guardar en base de datos
        Request savedRequest = requestRepository.save(request);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(requestMapper.toDto(savedRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRequest(@PathVariable(name = "id") Long id) {
        Optional<Request> find = requestRepository.findById(id);
        if (find.isPresent()) {
            RequestDto requestDto = requestMapper.toDto(find.get());
            requestRepository.delete(find.get());
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(requestDto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("No es aceptable");
        }
    }
    @DeleteMapping("/all")
    public ResponseEntity<?> deleteRequestAll() {
        requestRepository.deleteAll();
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("Hecho");
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateRequest(@PathVariable(name="id") Long id, @RequestBody RequestDto requestDto) throws BusinessRuleException {
        if (requestDto != null) {
            RequestDto dto = requestService.updateRequest(id, requestDto);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(dto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("No es aceptable");
        }
    }
    
}
