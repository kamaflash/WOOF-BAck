package com.pet.businessdomain.petservice.common;

import com.pet.businessdomain.petservice.dto.MatchDto;
import com.pet.businessdomain.petservice.dto.UserDto;

import java.util.List;

public interface IBusinessTransactions {
    UserDto getUser(Long id);
    List<MatchDto> getMatch(Long uid);
}
