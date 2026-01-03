/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.pet.businessdomain.userservice.services;

import com.pet.businessdomain.userservice.dto.UserDto;
import com.pet.businessdomain.userservice.entities.User;
import com.pet.businessdomain.userservice.exceptions.BusinessRuleException;
import java.util.List;
import java.util.Optional;


/**
 *
 * @author Pc
 */

public interface UserService {
    List<UserDto> getAllUsers();
    Optional<User> getUserById(Long id);
    UserDto createUser(User user);
    UserDto updateUser(Long id, UserDto userDto) throws BusinessRuleException;
    void deleteUser(Long id);
    UserDto getFull(Long uid) throws BusinessRuleException;
    List<UserDto> getFullList(Long id) throws BusinessRuleException;
    List<UserDto> getUserSheltter(String accountType);
    Optional<User> getUserByUsername(String username);
    Optional<User> getUserByEmail(String username);
    Optional<User> getUserByUsernameOrEmail(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String username);

}
