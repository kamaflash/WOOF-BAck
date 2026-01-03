package com.pet.businessdomain.requestservice.exceptions;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import com.pet.businessdomain.requestservice.common.StandarizedApiExeptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 *
 * @author Pc
 */
@RestControllerAdvice
public class ApiExceptionHadler {
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> hadlerUnknownHostException(Exception ex) {
        StandarizedApiExeptionResponse standarizedApiExeptionResponse = new StandarizedApiExeptionResponse("Tecnico", "Input Ouput error", "0001", ex.getMessage(), ex.getLocalizedMessage());
        return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(standarizedApiExeptionResponse);
    }
    
    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<?> hadlerBusinessRuleException(BusinessRuleException ex) {
        StandarizedApiExeptionResponse standarizedApiExeptionResponse = new StandarizedApiExeptionResponse("Business", "Error", ex.getCode(), ex.getMessage(), ex.getLocalizedMessage());
        return  ResponseEntity.status(ex.getHttpStatus()).body(standarizedApiExeptionResponse);
    }
}
