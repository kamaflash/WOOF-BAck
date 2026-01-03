/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.pet.businessdomain.petservice.repository;

import com.pet.businessdomain.petservice.dto.PetDto;
import com.pet.businessdomain.petservice.entities.Pet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 *
 * @author Pc
 */
public interface PetRepository extends JpaRepository<Pet, Long> {
    Page<Pet> findByUid(Long userId, Pageable pageable);
    List<Pet> findByUid(Long userId);
    Page<Pet> findByUidOrderByCreatedAtDesc(Long userId, Pageable pageable);
    Page<Pet> findByUidAndStatus(Long userId, String status, Pageable pageable);
    Page<Pet> findByUidAndName(Long userId, String name, Pageable pageable);
    Page<Pet> findByUidAndNameAndStatus(Long userId, String name, String status, Pageable pageable);
    List<Pet> findByStatus(String status);
    Page<Pet> findByUidNotAndStatusOrderByCreatedAtAsc(Long uid, String status, Pageable pageable);
    Page<Pet> findByStatusOrderByCreatedAtAsc(String status, Pageable pageable);
}
