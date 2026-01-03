package com.pet.businessdomain.notificationservice.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ICloudinaryService {
    String uploadFile(MultipartFile file) throws IOException;
    String deleteFile(String imageName) throws IOException;
}
