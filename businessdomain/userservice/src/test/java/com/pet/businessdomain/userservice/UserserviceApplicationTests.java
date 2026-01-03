package com.pet.businessdomain.userservice;

import com.pet.businessdomain.userservice.controller.UserController;
import com.pet.businessdomain.userservice.dto.UserDto;
import com.pet.businessdomain.userservice.entities.User;
import com.pet.businessdomain.userservice.mapper.UserMapper;
import com.pet.businessdomain.userservice.repository.UserRepository;
import com.pet.businessdomain.userservice.services.IEmailService;
import com.pet.businessdomain.userservice.services.ILocationService;
import com.pet.businessdomain.userservice.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerFullTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private IEmailService emailService;

    @MockBean
    private UserMapper userMapper;

    @MockBean
    private ILocationService locationService;

    @MockBean
    private PasswordEncoder passwordEncoder;
    @MockBean
    private UserRepository userRepository;

    private UserDto testUserDto;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUserDto = new UserDto();
        testUserDto.setId(1L);
        testUserDto.setUsername("testuser");

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("encodedPassword");
    }

    @Test
    void testGetAllUsers_nonEmptyList() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(testUserDto));

        mockMvc.perform(get("/api/users")
                        .with(user("admin").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("testuser"));
    }

    @Test
    void testGetUserById_exists() throws Exception {
        when(userService.getUserById(1L)).thenReturn(Optional.of(testUser));
        when(userMapper.toOptional(Optional.of(testUser))).thenReturn(testUser);
        when(userMapper.toDto(testUser)).thenReturn(testUserDto);

        mockMvc.perform(get("/api/users/1")
                        .with(user("admin").roles("USER")))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void testGetUserById_notExists() throws Exception {
        when(userService.getUserById(2L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/2")
                        .with(user("admin").roles("USER")))
                .andExpect(status().isNoContent());
    }

    @Test
    void testCheckUsernameExists_true() throws Exception {
        when(userService.existsByUsername("testuser")).thenReturn(true);

        mockMvc.perform(get("/api/users/exist/username/testuser")
                        .with(user("admin").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(true));
    }

    @Test
    void testCheckUsernameExists_false() throws Exception {
        when(userService.existsByUsername("newuser")).thenReturn(false);

        mockMvc.perform(get("/api/users/exist/username/newuser")
                        .with(user("admin").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(false));
    }

    @Test
    void testCheckEmailExists_true() throws Exception {
        when(userService.existsByEmail("test@mail.com")).thenReturn(true);

        mockMvc.perform(get("/api/users/exist/email/test@mail.com")
                        .with(user("admin").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(true));
    }

    @Test
    void testRegisterUser_sendsEmail() throws Exception {
        Mockito.doNothing().when(emailService).sendConfirmationEmail(any(UserDto.class));

        mockMvc.perform(post("/api/users/register")
                        .with(user("admin").roles("USER"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"testuser\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void testValidateUser_success() throws Exception {
        when(userService.getUserByUsernameOrEmail("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        when(userService.getFull(1L)).thenReturn(testUserDto);

        mockMvc.perform(post("/api/users/validate")
                        .with(user("admin").roles("USER"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"usernamemail\":\"testuser\",\"password\":\"123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void testValidateUser_fail() throws Exception {
        when(userService.getUserByUsernameOrEmail("wronguser")).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/users/validate")
                        .with(user("admin").roles("USER"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"usernamemail\":\"wronguser\",\"password\":\"123\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testDeleteAllUsers() throws Exception {
        mockMvc.perform(delete("/api/users/all")
                        .with(user("admin").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isAccepted());
    }

    @Test
    void testDeleteUser_exists() throws Exception {
        // Mock del repositorio para que "encuentre" el usuario
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Mock del mapper
        when(userMapper.toDto(testUser)).thenReturn(testUserDto);

        mockMvc.perform(delete("/api/users/1")
                        .with(user("admin").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isAccepted());
    }


    @Test
    void testDeleteUser_notExists() throws Exception {
        mockMvc.perform(delete("/api/users/99")
                        .with(user("admin").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isNotAcceptable());
    }
}
