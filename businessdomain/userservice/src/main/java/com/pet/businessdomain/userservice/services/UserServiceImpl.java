/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pet.businessdomain.userservice.services;

import com.pet.businessdomain.userservice.dto.UserDto;
import com.pet.businessdomain.userservice.entities.User;
import com.pet.businessdomain.userservice.exceptions.BusinessRuleException;
import com.pet.businessdomain.userservice.mapper.UserMapper;
import com.pet.businessdomain.userservice.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.pet.businessdomain.userservice.transactions.BusinessTransactions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

/**
 *
 * @author Pc
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Autowired
    private BusinessTransactions businessTransactions;
    // Add any required dependencies here (e.g., repositories, mappers)

    @Override
    public List<UserDto> getAllUsers() {
        List<UserDto> listUserDto = userMapper.toDtoList(userRepository.findAll());

        return listUserDto;
    }

    @Override
    public Optional<User> getUserById(Long id) {
        Optional<User> opt = userRepository.findById(id);
        return opt;
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        Optional<User> opt = userRepository.findByUsername(username);
        return opt;
    }

    @Override
    public Optional<User> getUserByEmail(String username) {
        Optional<User> opt = userRepository.findByEmail(username);
        return opt;
    }
    @Override
    public List<UserDto> getUserSheltter(String accountType) {
        List<User> listUser = userRepository.findByAccountType(accountType);

        return userMapper.toDtoList(listUser);
    }

    @Override
    public Optional<User> getUserByUsernameOrEmail(String username) {
        Optional<User> opt = userRepository.findUserByUsernameOrEmail(username, username);
        return opt;
    }


    @Override
    public UserDto getFull(Long id) throws BusinessRuleException  {
        // Implementation
        Optional<User> optUser = userRepository.findById(id);
        User user = userMapper.toOptional(optUser);
        List<?> pet = businessTransactions.getPet(id);

        if (user != null) {
            UserDto dto = userMapper.toDto(user);
            dto.setPet(pet);
            return dto;
        } else {
            BusinessRuleException businessRuleException = new BusinessRuleException("0002", "Error validación. Transacion no localizada. ", HttpStatus.PRECONDITION_FAILED);
            throw businessRuleException;
        }
    }
    @Override
    public List<UserDto> getFullList(Long id) throws BusinessRuleException  {
        // Implementation
        Optional<User> optUser = userRepository.findById(id);
        User user = userMapper.toOptional(optUser);
        List<User> listUser = new ArrayList<>();
        listUser.add(user);

        if (user != null) {
            List<UserDto> dtoList = userMapper.toDtoList(listUser);
            return dtoList;
        } else {
            BusinessRuleException businessRuleException = new BusinessRuleException("0002", "Error validación. Transacion no localizada. ", HttpStatus.PRECONDITION_FAILED);
            throw businessRuleException;
        }
    }

    @Override
    public UserDto createUser(User user) {
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setRole("USER_ROLE");
        user = userRepository.save(user);
        return userMapper.toDto(user);
    }

    @Override
    public UserDto updateUser(Long id, UserDto userDto) throws BusinessRuleException {
        Optional<User> opt = userRepository.findById(id);
        log.info("Buscando usuario con ID: {}", id);

        if (opt.isEmpty()) {
            BusinessRuleException businessRuleException = new BusinessRuleException(
                    "0002",
                    "Error validación. Usuario no encontrado.",
                    HttpStatus.PRECONDITION_FAILED
            );
            throw businessRuleException;
        }

        User resUser = opt.get();
        log.info("Usuario encontrado: {}", resUser.getUsername());

        // Actualizar campos básicos
        resUser.setId(id);
        resUser.setName(userDto.getName());
        resUser.setBio(userDto.getBio());
        resUser.setLastname(userDto.getLastname());
        resUser.setUpdatedAt(LocalDateTime.now());
        resUser.setPhone(userDto.getPhone());
        resUser.setWebsite(userDto.getWebsite());
        resUser.setUsername(userDto.getUsername());
        resUser.setAvatarUrl(userDto.getAvatarUrl());
        resUser.setAddress(userDto.getAddress());
        resUser.setHasGarden(userDto.getHasGarden());
        resUser.setHasKids(userDto.getHasKids());
        resUser.setHasOtherPets(userDto.getHasOtherPets());
        resUser.setHouseType(userDto.getHouseType());
        resUser.setCif(userDto.getCif());
        resUser.setEmail(userDto.getEmail());
        resUser.setAcceptsDonations(userDto.getAcceptsDonations());
        resUser.setAcceptsVolunteers(userDto.getAcceptsVolunteers());
        resUser.setFeedback(userDto.getFeedback());
        resUser.setOpeningHours(userDto.getOpeningHours());
        resUser.setIsOpen24h(userDto.getIsOpen24h());
        resUser.setHasVeterinarian(userDto.getHasVeterinarian());


        // IMPORTANTE: Solo actualizar password si se proporciona uno nuevo
        if (userDto.getPassword() != null && !userDto.getPassword().trim().isEmpty()) {
            // Considera encriptar la contraseña aquí si es necesario
            resUser.setPassword(userDto.getPassword());
        }

        // ----------------------------------------------------------
        // ACTUALIZAR NUEVOS CAMPOS PARA MATCH AUTOMÁTICO
        // ----------------------------------------------------------

        // Diferenciar qué mascotas tiene
        if (userDto.getHasOtherDogs() != null) {
            resUser.setHasOtherDogs(userDto.getHasOtherDogs());
        }

        if (userDto.getHasOtherCats() != null) {
            resUser.setHasOtherCats(userDto.getHasOtherCats());
        }

        // Nivel de experiencia con animales
        if (userDto.getExperienceWithPets() != null) {
            // Validar que sea uno de los valores permitidos
            String experience = userDto.getExperienceWithPets();
            resUser.setExperienceWithPets(experience);

        }

        // Actividad del usuario
        if (userDto.getActivityLevel() != null) {
            String activity = userDto.getActivityLevel();
            resUser.setActivityLevel(activity);
        }

        // Tiempo en casa
        if (userDto.getTimeAtHome() != null) {
            String timeAtHome = userDto.getTimeAtHome();
            resUser.setTimeAtHome(timeAtHome);
        }

        // Capacidad económica
        if (userDto.getBudgetLevel() != null) {
            String budget = userDto.getBudgetLevel();
            resUser.setBudgetLevel(budget);
        }

        // Restricciones de vivienda
        if (userDto.getAllowsLargePets() != null) {
            resUser.setAllowsLargePets(userDto.getAllowsLargePets());
        }

        // Tipo de cuenta
        if (userDto.getAccountType() != null) {
            resUser.setAccountType(userDto.getAccountType());
        }

        // Estado habilitado
        resUser.setEnabled(userDto.isEnabled());

        log.info("Guardando cambios del usuario: {}", resUser.getUsername());

        try {
            User savedUser = userRepository.save(resUser);
            UserDto savedDto = userMapper.toDto(savedUser);
            log.info("Usuario actualizado exitosamente: {}", savedDto.getUsername());
            return savedDto;
        } catch (Exception e) {
            log.error("Error al guardar usuario: {}", e.getMessage(), e);
            BusinessRuleException businessRuleException = new BusinessRuleException(
                    "0003",
                    "Error al guardar los cambios del usuario: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
            throw businessRuleException;
        }
    }

    @Override
    public void deleteUser(Long id) {
        // Implementation here
    }
    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String username) {
        return userRepository.existsByEmail(username);
    }
}
