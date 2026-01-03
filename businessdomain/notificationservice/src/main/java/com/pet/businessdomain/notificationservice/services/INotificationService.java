/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.pet.businessdomain.notificationservice.services;

import com.pet.businessdomain.notificationservice.dto.NotificationDto;
import com.pet.businessdomain.notificationservice.entities.Notification;
import com.pet.businessdomain.notificationservice.exceptions.BusinessRuleException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 *
 * @author Pc
 */

public interface INotificationService {
    NotificationDto getUserByPet(Notification notification) throws BusinessRuleException;
    NotificationDto updatePet(Long id, NotificationDto notificationDto) throws BusinessRuleException;
    List<NotificationDto> getNotificationByUid(Long uid) throws BusinessRuleException;
    Notification acceptNotification(Long id) throws BusinessRuleException;
    List<NotificationDto> getNotificationByAid(Long uid) throws BusinessRuleException;
    Notification rejectNotification(Long id) throws BusinessRuleException;
    boolean checkNotification(Long uid, Long aid);
    Page<NotificationDto> getNotificationsByUidWithUserAndPet(Long uid, Pageable pageable);
}
