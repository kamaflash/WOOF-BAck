/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.pet.businessdomain.requestservice.repository;

import com.pet.businessdomain.requestservice.entities.Request;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 *
 * @author Pc
 */
public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findByUserId(Long userId);
    Page<Request> findByUserId(Long userId, Pageable pageable);
    List<Request> findByPetId(Long userId);
    List<Request> findByProUid(Long userId);
    Page<Request> findByProUid(Long userId, Pageable pageable);
    Page<Request> findByPetId(Long userId, Pageable pageable);
    Page<Request> findByUserIdAndPetId(Long userId,Long petId, Pageable pageable);
    List<Request> findByUserIdAndPetId(Long userId,Long petId);
    @Query("SELECT r FROM Request r WHERE r.proUid = :uid OR r.userId = :uid ORDER BY r.updateAt DESC")
    Page<Request> findByProUidOrUserId(@Param("uid") Long uid, Pageable pageable);
    @Query("SELECT r FROM Request r WHERE (r.proUid = :uid OR r.userId = :uid) AND r.status = :status ORDER BY r.updateAt DESC")
    Page<Request> findByProUidOrUserIdAndStatus(@Param("uid") Long uid, @Param("status") String status, Pageable pageable);

}
