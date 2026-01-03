/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.pet.businessdomain.notificationservice.repository;

import com.pet.businessdomain.notificationservice.entities.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 *
 * @author Pc
 */
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUid(Long userId);
    Page<Notification> findByUid(Long userId, Pageable pageable);

    List<Notification> findByAid(Long aid);
    Optional<Notification> getNotificationByUidAndAid(Long uid,Long aid);
    List<Notification> getNotificationByUid(Long uid);
}
