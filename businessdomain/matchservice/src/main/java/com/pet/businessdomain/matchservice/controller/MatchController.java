/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pet.businessdomain.matchservice.controller;


import com.pet.businessdomain.matchservice.common.BusinessTransactions;
import com.pet.businessdomain.matchservice.dto.LikeDto;
import com.pet.businessdomain.matchservice.dto.MatchDto;
import com.pet.businessdomain.matchservice.dto.UserDto;
import com.pet.businessdomain.matchservice.entities.Match;
import com.pet.businessdomain.matchservice.exceptions.BusinessRuleException;
import com.pet.businessdomain.matchservice.services.ICloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;

import com.pet.businessdomain.matchservice.mapper.MatchMapper;
import com.pet.businessdomain.matchservice.repository.MatchRepository;
import com.pet.businessdomain.matchservice.services.IMatchService;

/**
 *
 * @author Pc
 */
@RestController
@RequestMapping("/api/match")
public class MatchController {

    @Autowired
    private IMatchService matchService;
    @Autowired
    private MatchRepository matchRepository;
    @Autowired
    private MatchMapper matchMapper;
    @Autowired
    private ICloudinaryService cloudinaryService;
    @Autowired
    private BusinessTransactions businessTransactions;

    private int SIZE = 10;
    // --------------------------------------------------------
    // GET: Obtener TODOS los matches
    // --------------------------------------------------------
    @GetMapping
    public ResponseEntity<?> getAllPMath() {
        List<MatchDto> listMatchDto = matchMapper.toDtoList(matchRepository.findAll());
        if (listMatchDto.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No existen match");
        } else {
            return ResponseEntity.ok(listMatchDto);
        }
    }

    @GetMapping("/user/{uid}")
    public ResponseEntity<?> getAllPetsById(
            @RequestParam(name = "page",defaultValue = "0") int page,
            @RequestParam(name = "status",defaultValue = "all") String status,
            @RequestParam(name = "search",defaultValue = "all") String search,
            @PathVariable(name = "uid") Long uid) {
        int size = 0;
        if(page == 0) {
            size=9;
        } else {
            size = SIZE;
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<Match> matchsPage = matchRepository.findByUid(uid,pageable);
        if(!status.equals("all") && search.equals("all")){
            matchsPage = matchRepository.findByUidAndStatus(uid,status,pageable);
        }

        Page<MatchDto> petsDtoPage = matchsPage.map(matchMapper::toDto);
        petsDtoPage.forEach(matchDto -> {
            var pet = businessTransactions.getPet(matchDto.getAid(),0L);
            matchDto.setPet(pet);
        });
        if (matchsPage.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No existen mascotas");
        }

        List<MatchDto> listMatchDto = petsDtoPage.getContent();
        Map<String, Object> response = new HashMap<>();
        response.put("matchs", listMatchDto);
        response.put("currentPage", matchsPage.getNumber());
        response.put("totalItems", matchsPage.getTotalElements());
        response.put("totalPages", matchsPage.getTotalPages());
        response.put("pageSize", matchsPage.getSize());
        response.put("hasNext", matchsPage.hasNext());
        response.put("hasPrevious", matchsPage.hasPrevious());

        return ResponseEntity.ok(response);
    }
    // --------------------------------------------------------
    // GET: Obtener matches por usuario
    // --------------------------------------------------------
//    @GetMapping("/user/{uid}")
//    public ResponseEntity<List<MatchDto>> getByUser(@PathVariable(name = "uid")  Long uid) throws BusinessRuleException {
//        List<MatchDto> listMatchDto = matchService.getMatchByUid(uid);
//        if (listMatchDto.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
//        } else {
//            return ResponseEntity.ok(listMatchDto);
//        }
//    }

    // --------------------------------------------------------
    // GET: Obtener matches por animal
    // --------------------------------------------------------
    @GetMapping("/pet/{aid}")
    public ResponseEntity<?> getByAnimal(
            @RequestParam(name = "page",defaultValue = "0") int page,
            @RequestParam(name = "status",defaultValue = "all") String status,
            @RequestParam(name = "search",defaultValue = "all") String search,
            @PathVariable(name = "aid") Long aid) {
        int size = 0;
        if(page == 0) {
            size=9;
        } else {
            size = SIZE;
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<Match> matchsPage = matchRepository.findByAid(aid,pageable);
        if(!status.equals("all") && search.equals("all")){
            matchsPage = matchRepository.findByAidAndStatus(aid,status,pageable);
        }

        Page<MatchDto> petsDtoPage = matchsPage.map(matchMapper::toDto);
        petsDtoPage.forEach(matchDto -> {
            var pet = businessTransactions.getPet(aid,0L);
            matchDto.setPet(pet);
        });
        petsDtoPage.forEach(matchDto -> {
            var user = businessTransactions.getUsera(matchDto.getUid());
            matchDto.setUserdto(user);
        });
        if (matchsPage.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No existen mascotas");
        }

        List<MatchDto> listMatchDto = petsDtoPage.getContent();
        Map<String, Object> response = new HashMap<>();
        response.put("matchs", listMatchDto);
        response.put("currentPage", matchsPage.getNumber());
        response.put("totalItems", matchsPage.getTotalElements());
        response.put("totalPages", matchsPage.getTotalPages());
        response.put("pageSize", matchsPage.getSize());
        response.put("hasNext", matchsPage.hasNext());
        response.put("hasPrevious", matchsPage.hasPrevious());

        return ResponseEntity.ok(response);
    }
//    @GetMapping("/animal/{aid}")
//    public ResponseEntity<List<MatchDto>> getByAnimal(@PathVariable(name = "aid")  Long aid) throws BusinessRuleException {
//        List<MatchDto> listMatchDto = matchService.getMatchByAid(aid);
//        if (listMatchDto.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
//        } else {
//            return ResponseEntity.ok(listMatchDto);
//        }
//    }

    // --------------------------------------------------------
    // POST: Crear un match (petición realizada)
    // --------------------------------------------------------
    @PostMapping
    public ResponseEntity<MatchDto> createPet(@RequestBody MatchDto matchDto) {
        // Convertir DTO a Entidad
        Match match = matchMapper.toEntity(matchDto);
        match.setCreatedAt(LocalDateTime.now());
        LikeDto likeDto = new LikeDto();
        likeDto.setAid(match.getAid());
        likeDto.setUid(match.getUid());
        if(likeAnimal( likeDto)) {
            match.setStatus("Aceptado");
            match.setAceptAt(LocalDateTime.now());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(matchMapper.toDto(matchRepository.save(match)));
    }

    // --------------------------------------------------------
    // PUT: Aceptar un match
    // --------------------------------------------------------
    @PutMapping("/{id}/accept")
    public ResponseEntity<Match> acceptMatch(@PathVariable Long id) throws BusinessRuleException {
        Match updated = matchService.acceptMatch(id);
        if (updated == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        } else {
            return ResponseEntity.ok(updated);
        }
    }

    // --------------------------------------------------------
    // PUT: Rechazar un match
    // --------------------------------------------------------
    @PutMapping("/{id}/reject")
    public ResponseEntity<Match> rejectMatch(@PathVariable Long id) throws BusinessRuleException {
        Match updated = matchService.rejectMatch(id);
        if (updated == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        } else {
            return ResponseEntity.ok(updated);
        }
    }







    @GetMapping("/uid/{uid}")
    public ResponseEntity<?> getMatchByUid(@PathVariable(name = "uid") Long uid) throws BusinessRuleException {
        List<MatchDto> list = matchService.getMatchByUid(uid);
        if (!list.isEmpty()) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(list);
        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No existe la obra");
        }
    }

    @GetMapping("/user/{uid}/pet/{aid}")
    public ResponseEntity<?> getMatchByUidAndAid(@PathVariable(name = "uid") Long uid, @PathVariable(name = "aid") Long aid) throws BusinessRuleException {
        Optional<Match> opt = matchRepository.getMatchByUidAndAid(uid,aid);
        Match match = matchMapper.toOptional(opt);
        MatchDto matchDto = matchService.getUserByPet(match);
        if (opt.isPresent() && matchDto != null) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(matchDto);
        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No existe la obra");
        }
    }

    @GetMapping("/match/full")
    public ResponseEntity<?> get(@RequestParam(name = "uid") Long uid) {
        List<Match> matchs = matchRepository.findByUid(uid);
        List<MatchDto> matchsList = matchMapper.toDtoList(matchs);

        if (matchsList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).build();
        } else {
            return ResponseEntity.ok(matchsList);
        }
    }

    @DeleteMapping("/all")
    public ResponseEntity<?> deleteAll() {
        matchRepository.deleteAll();
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("Hecho");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePet(@PathVariable(name = "id") Long id) {
        Optional<Match> find = matchRepository.findById(id);
        if (find.isPresent()) {
            MatchDto matchDto = matchMapper.toDto(find.get());
            matchRepository.delete(find.get());
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(matchDto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("No es aceptable");
        }
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePet(@PathVariable(name="id") Long id, @RequestBody MatchDto matchDto) throws BusinessRuleException {
        if (matchDto != null) {
            MatchDto dto = matchService.updatePet(id, matchDto);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(dto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("No es aceptable");
        }
    }

    private boolean likeAnimal( LikeDto request) {


        return matchService.checkMatch(request.getUid(), request.getAid());
    }

}
