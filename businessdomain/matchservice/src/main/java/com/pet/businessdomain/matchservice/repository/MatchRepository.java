/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.pet.businessdomain.matchservice.repository;

import com.pet.businessdomain.matchservice.entities.Match;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 *
 * @author Pc
 */
public interface MatchRepository extends JpaRepository<Match, Long> {
    List<Match> findByUid(Long userId);
    Page<Match> findByUid(Long userId, Pageable pageable);
    Page<Match> findByUidAndStatus(Long userId, String status, Pageable pageable);

    List<Match> findByAid(Long aid);

    Page<Match> findByAid(Long animalId, Pageable pageable);
    Page<Match> findByAidAndStatus(Long animalId, String status, Pageable pageable);
    Optional<Match> getMatchByUidAndAid(Long uid,Long aid);
    List<Match> getMatchByUid(Long uid);
}
