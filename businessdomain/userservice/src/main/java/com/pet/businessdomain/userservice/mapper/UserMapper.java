/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.pet.businessdomain.userservice.mapper;

import com.pet.businessdomain.userservice.dto.UserDto;
import com.pet.businessdomain.userservice.entities.User;
import java.util.List;
import java.util.Optional;
import org.mapstruct.Mapper;

/**
 *
 * @author Pc
 */
@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);
    User toEntity(UserDto userDto);
    List<UserDto> toDtoList(List<User> users);
    default User toOptional(Optional<User> opt) {
        return opt.orElse(null); // o lanza una excepción si lo prefieres
    }
}
