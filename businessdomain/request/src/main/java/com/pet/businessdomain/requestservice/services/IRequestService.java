/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.pet.businessdomain.requestservice.services;

import com.pet.businessdomain.requestservice.dto.RequestDto;
import com.pet.businessdomain.requestservice.entities.Request;
import com.pet.businessdomain.requestservice.exceptions.BusinessRuleException;

import java.util.List;

/**
 *
 * @author Pc
 */

public interface IRequestService {
    List<RequestDto> getAllRequests();
    RequestDto getRequestById(Long id);
    RequestDto getUserByRequest(RequestDto request) throws BusinessRuleException;
    RequestDto getAnimalByRequest(RequestDto request) throws BusinessRuleException;
    RequestDto createRequest(RequestDto requestDto);
    RequestDto updateRequest(Long id, RequestDto requestDto) throws BusinessRuleException;
    void deleteRequest(Long id);
    RequestDto getFull(Long id) throws BusinessRuleException;
}
