/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.pet.businessdomain.matchservice.services;

import com.pet.businessdomain.matchservice.dto.MatchDto;
import com.pet.businessdomain.matchservice.entities.Match;
import com.pet.businessdomain.matchservice.exceptions.BusinessRuleException;

import java.util.List;

/**
 *
 * @author Pc
 */

public interface IMatchService {
    MatchDto getUserByPet(Match match) throws BusinessRuleException;
    MatchDto updatePet(Long id, MatchDto matchDto) throws BusinessRuleException;
    List<MatchDto> getMatchByUid(Long uid) throws BusinessRuleException;
    Match acceptMatch(Long id) throws BusinessRuleException;
    List<MatchDto> getMatchByAid(Long uid) throws BusinessRuleException;
    Match rejectMatch(Long id) throws BusinessRuleException;
    boolean checkMatch(Long uid, Long aid);
}
