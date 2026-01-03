package com.pet.businessdomain.userservice.services;

import com.pet.businessdomain.userservice.dto.UserDto;
import jakarta.mail.MessagingException;

public interface IEmailService {
    void sendConfirmationEmail(UserDto user) throws MessagingException;
}
