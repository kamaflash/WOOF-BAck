package com.pet.businessdomain.matchservice.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryServiceImpl implements ICloudinaryService {
    private Cloudinary cloudinary = new Cloudinary();

    public CloudinaryServiceImpl() {
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dfnywietn",
                "api_key", "167864469956794",
                "api_secret", "3B_SLezQf3uYOz4kwqEzUbj35rQ"
        ));
    }

    public String uploadFile(MultipartFile file) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
        return (String) uploadResult.get("secure_url"); // Devuelve la URL segura
    }

    public String deleteFile(String imageName) throws IOException {
        // Extraemos el public_id del string recibido
        String[] parts = imageName.split("/");
        String filename = parts[parts.length - 1]; // ej: perrito.jpg
        String publicId = filename.contains(".")
                ? filename.substring(0, filename.lastIndexOf('.'))
                : filename;


        // Eliminamos de Cloudinary
        Map result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());

        // Comprobamos si se eliminó correctamente
        if ("ok".equals(result.get("result"))) {
            return "Imagen eliminada correctamente";
        } else {
            return "No se pudo eliminar: " + result.get("result");
        }
    }
}
