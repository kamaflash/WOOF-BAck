package com.pet.businessdomain.userservice.dto;

import lombok.Data;

@Data
public class LoginDto {
    private String usernamemail;
    private String password;
}
