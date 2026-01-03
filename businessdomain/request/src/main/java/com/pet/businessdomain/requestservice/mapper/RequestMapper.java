/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.pet.businessdomain.requestservice.mapper;

import com.pet.businessdomain.requestservice.dto.RequestDto;
import com.pet.businessdomain.requestservice.entities.Request;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Optional;

/**
 *
 * @author Pc
 */
@Mapper(componentModel = "spring")
public interface RequestMapper {
    RequestDto toDto(Request request);
    Request toEntity(RequestDto requestDto);
    List<RequestDto> toDtoList(List<Request> requests);
    default Request toOptional(Optional<Request> opt) {
        return opt.orElse(null); // o lanzar excepción si prefieres
    }
}
