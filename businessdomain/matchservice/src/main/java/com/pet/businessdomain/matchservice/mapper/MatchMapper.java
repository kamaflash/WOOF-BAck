/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.pet.businessdomain.matchservice.mapper;

import com.pet.businessdomain.matchservice.dto.MatchDto;
import com.pet.businessdomain.matchservice.entities.Match;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Optional;

/**
 *
 * @author Pc
 */
@Mapper(componentModel = "spring")
public interface MatchMapper {
    MatchDto toDto(Match match);
    Match toEntity(MatchDto matchDto);
    List<MatchDto> toDtoList(List<Match> matchs);
    default Match toOptional(Optional<Match> opt) {
        return opt.orElse(null); // o lanzar excepción si prefieres
    }
}
