/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pet.businessdomain.userservice.controller;

import com.pet.businessdomain.userservice.dto.LoginDto;
import com.pet.businessdomain.userservice.dto.UserDto;
import com.pet.businessdomain.userservice.entities.User;
import com.pet.businessdomain.userservice.exceptions.BusinessRuleException;
import com.pet.businessdomain.userservice.mapper.UserMapper;
import com.pet.businessdomain.userservice.repository.UserRepository;
import com.pet.businessdomain.userservice.services.IEmailService;
import com.pet.businessdomain.userservice.services.ILocationService;
import com.pet.businessdomain.userservice.services.UserService;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author Pc
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
public class UserController {
    private static final int SIZE = 5;
    private static final String SHELTTER = "shelter";
    private static final String MESSAGE = "message";
    private static final String RESULT = "result";
    private static final String NOTEXIT = "No existe el usuario";

    @Autowired
    private UserService userService;
    @Autowired
    private IEmailService emailService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ILocationService locationService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        List<UserDto> listUserDto = userService.getAllUsers();
        if (listUserDto.isEmpty()) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).build();
        } else {
            return ResponseEntity.ok(listUserDto);
        }
    }

    @GetMapping(("shelter/{uid}"))
    public ResponseEntity<?> getAllUsersShelter(@PathVariable(name = "uid") Long uid,
                                                @RequestParam(name = "page",defaultValue = "0") int page) throws BusinessRuleException {

        Pageable pageable = PageRequest.of(page, SIZE);
        Page<User> usersPage = userRepository.findByAccountType(SHELTTER, pageable);
        if(uid != 0) {
            usersPage = userRepository.findByIdNotAndAccountType(uid, SHELTTER, pageable);
        }
        Page<UserDto> usersDtoPage = usersPage.map(userMapper::toDto);
        if (usersDtoPage.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No existen mascotas urgentes");
        }
        Optional<User> userOptional = userRepository.findById(uid);
        List<UserDto> usersListDto = usersDtoPage.getContent();

        if (userOptional.isPresent()) {
            String userAddress = userOptional.get().getAddress();
            for (int i = 0; i < usersListDto.size(); i++) {
                UserDto petDto = usersListDto.get(i);
                User pet = usersPage.getContent().get(i);

                double distanceKm = locationService
                        .distanceBetweenAddresses(userAddress, pet.getAddress());
                petDto.setDistance(distanceKm);
            }
        }





        Map<String, Object> response = new HashMap<>();
        response.put("shelter", usersListDto);
        response.put("currentPage", usersPage.getNumber());
        response.put("totalItems", usersPage.getTotalElements());
        response.put("totalPages", usersPage.getTotalPages());
        response.put("pageSize", usersPage.getSize());
        response.put("hasNext", usersPage.hasNext());
        response.put("hasPrevious", usersPage.hasPrevious());

        return ResponseEntity.ok(response);
    }

    @GetMapping("exist/username/{username}")
    public ResponseEntity<?> checkUsernameExists(@PathVariable("username") String username) {
        boolean exists = userService.existsByUsername(username);

        if (exists) {
            return ResponseEntity.ok(Map.of(MESSAGE, "El nombre de usuario ya existe.",RESULT, true));

        } else {
            return ResponseEntity.ok(Map.of(MESSAGE, "Bien",RESULT, false));
        }
    }
    @GetMapping("exist/email/{email}")
    public ResponseEntity<?> checkEmailExists(@PathVariable("email") String email) {
        boolean exists = userService.existsByEmail(email);

        if (exists) {
            return ResponseEntity.ok(Map.of(MESSAGE, "El email ya existe.",RESULT, true));

        } else {
            return ResponseEntity.ok(Map.of(MESSAGE, "Bien",RESULT, false));
        }
    }

    @GetMapping("/fulluser/{id}")
    public ResponseEntity<?> getUserByIdFull(@PathVariable(name = "id") Long id) throws BusinessRuleException {

        UserDto save = userService.getFull(id);

        if (save != null) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(save);
        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(NOTEXIT);
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable(name = "id") Long id) throws BusinessRuleException {

        Optional<User> optionalUserDto = userService.getUserById(id);
        User user = userMapper.toOptional(optionalUserDto);
        UserDto userDto = userMapper.toDto(user);

        if (userDto != null) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(userDto);
        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(NOTEXIT);
        }
    }
    @GetMapping("/seltter/{id}")
    public ResponseEntity<?> getUserSheltter(@PathVariable(name = "accountType") String accountType) throws BusinessRuleException {

        List<UserDto> save = userService.getUserSheltter(accountType);

        if (save != null) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(save);
        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(NOTEXIT);
        }
    }
    @GetMapping("/pet/{id}")
    public ResponseEntity<?> getUserByIdForPet(@PathVariable(name = "id") Long id) throws BusinessRuleException {

        List<UserDto> save = userService.getFullList(id);

        if (save != null) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(save);
        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(NOTEXIT);
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validateUser(@RequestBody LoginDto login) throws BusinessRuleException {

        Optional<User> optUser = userService.getUserByUsernameOrEmail(login.getUsernamemail());

        if (optUser.isPresent()) {
            User user = optUser.get();

            if (passwordEncoder.matches(login.getPassword(), user.getPassword())) {
                UserDto dto = userService.getFull(user.getId());
                return ResponseEntity.ok(dto);
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
    }

    @GetMapping("/full/{uid}")
    public ResponseEntity<?> getFull(@PathVariable(name = "uid") Long uid) throws BusinessRuleException {
        UserDto save = userService.getFull(uid);

        if (save != null) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(save);
        } else {
            return ResponseEntity.status(HttpStatus.ACCEPTED).build();
        }
    }
    @PostMapping("/register")
    public ResponseEntity<?>  geRegister(@RequestBody UserDto userDto) throws BusinessRuleException, MessagingException {

        emailService.sendConfirmationEmail(userDto);
        return ResponseEntity.ok(Map.of(MESSAGE, "Se te ha enviado un mail a la dirección facilitada. Revise para confirmar registro."));
    }
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) throws BusinessRuleException, UnknownHostException {
        // Convertir DTO a Entidad
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        UserDto userDto = userService.createUser(user);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(userDto);
    }
    @DeleteMapping("/all")
    public ResponseEntity<?> deleteAll() {
        userRepository.deleteAll();
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("Hecho");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable(name = "id") Long id) {
        Optional<User> find = userRepository.findById(id);
        if (find.isPresent()) {
            UserDto userDto = userMapper.toDto(find.get());
            userRepository.delete(find.get());
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(userDto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("No es aceptable");
        }
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable(name="id") Long id, @RequestBody UserDto userDto) throws BusinessRuleException {
        if (userDto != null) {
            UserDto dto = userService.updateUser(id, userDto);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(dto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("No es aceptable");
        }
    }
}
