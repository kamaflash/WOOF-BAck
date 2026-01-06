package com.pet.businessdomain.requestservice;

import com.pet.businessdomain.requestservice.controller.RequestController;
import com.pet.businessdomain.requestservice.dto.RequestDto;
import com.pet.businessdomain.requestservice.entities.Request;
import com.pet.businessdomain.requestservice.mapper.RequestMapper;
import com.pet.businessdomain.requestservice.repository.RequestRepository;
import com.pet.businessdomain.requestservice.services.ICloudinaryService;
import com.pet.businessdomain.requestservice.services.IRequestService;
import com.pet.businessdomain.requestservice.exceptions.BusinessRuleException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(RequestController.class)
class RequestControllerFullTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IRequestService requestService;

    @MockBean
    private RequestRepository requestRepository;

    @MockBean
    private RequestMapper requestMapper;

    @MockBean
    private ICloudinaryService cloudinaryService;

    private Request testRequest;
    private RequestDto testRequestDto;

    @BeforeEach
    void setUp() {
        testRequest = new Request();
        testRequest.setId(1L);
        testRequest.setCreatedAt(LocalDateTime.now());
        testRequest.setUpdateAt(LocalDateTime.now());

        testRequestDto = new RequestDto();
        testRequestDto.setId(1L);
    }

    @Test
    void testGetAllRequests_nonEmpty() throws Exception {
        when(requestRepository.findAll()).thenReturn(List.of(testRequest));
        when(requestMapper.toDtoList(List.of(testRequest))).thenReturn(List.of(testRequestDto));
        when(requestService.getUserByRequest(testRequestDto)).thenReturn(testRequestDto);

        mockMvc.perform(get("/api/request")
                        .with(user("admin").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void testGetRequestById_exists() throws Exception {
        when(requestRepository.findById(1L)).thenReturn(Optional.of(testRequest));
        when(requestMapper.toOptional(Optional.of(testRequest))).thenReturn(testRequest);
        when(requestMapper.toDto(testRequest)).thenReturn(testRequestDto);
        when(requestService.getUserByRequest(testRequestDto)).thenReturn(testRequestDto);

        mockMvc.perform(get("/api/request/1")
                        .with(user("admin").roles("USER")))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testGetRequestById_notExists() throws Exception {
        when(requestRepository.findById(2L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/request/2")
                        .with(user("admin").roles("USER")))
                .andExpect(status().isNoContent());
    }

    @Test
    void testCreateRequest() throws Exception {
        when(requestMapper.toEntity(any(RequestDto.class))).thenReturn(testRequest);
        when(requestRepository.save(any(Request.class))).thenReturn(testRequest);
        when(requestMapper.toDto(any(Request.class))).thenReturn(testRequestDto);

        mockMvc.perform(post("/api/request")
                        .with(user("admin").roles("USER"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1}"))
                .andExpect(status().isCreated())  // 201
                .andExpect(jsonPath("$.id").value(1));
    }


    @Test
    void testDeleteRequest_exists() throws Exception {
        when(requestRepository.findById(1L)).thenReturn(Optional.of(testRequest));
        when(requestMapper.toDto(testRequest)).thenReturn(testRequestDto);

        mockMvc.perform(delete("/api/request/1")
                        .with(user("admin").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isAccepted());
    }

    @Test
    void testDeleteRequest_notExists() throws Exception {
        when(requestRepository.findById(2L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/request/2")
                        .with(user("admin").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    void testDeleteRequestAll() throws Exception {
        mockMvc.perform(delete("/api/request/all")
                        .with(user("admin").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isAccepted());
    }

    @Test
    void testUpdateRequest() throws Exception {
        when(requestService.updateRequest(any(Long.class), any(RequestDto.class)))
                .thenReturn(testRequestDto);

        mockMvc.perform(put("/api/request/1")
                        .with(user("admin").roles("USER"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1}"))
                .andExpect(status().isAccepted()) // 202, no 201
                .andExpect(jsonPath("$.id").value(1));
    }

}
