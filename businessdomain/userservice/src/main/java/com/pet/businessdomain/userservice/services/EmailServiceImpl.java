package com.pet.businessdomain.userservice.services;

import com.pet.businessdomain.userservice.dto.UserDto;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class EmailServiceImpl implements IEmailService {

    private String appBaseUrl;
    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendConfirmationEmail(UserDto userDto) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        String baseUrl = "http://localhost:4200/confirm-registration";
        String queryParams = String.format(
                "?accountType=%s&username=%s&email=%s&name=%s&lastname=%s&phone=%s&address=%s&houseType=%s&hasGarden=%s&hasKids=%s&hasOtherPets=%s&cif=%s&website=%s",
                URLEncoder.encode(userDto.getAccountType(), StandardCharsets.UTF_8),
                URLEncoder.encode(userDto.getUsername(), StandardCharsets.UTF_8),
                URLEncoder.encode(userDto.getEmail(), StandardCharsets.UTF_8),
                URLEncoder.encode(userDto.getName(), StandardCharsets.UTF_8),
                URLEncoder.encode(userDto.getLastname(), StandardCharsets.UTF_8),
                URLEncoder.encode(userDto.getPhone(), StandardCharsets.UTF_8),
                URLEncoder.encode(userDto.getAddress(), StandardCharsets.UTF_8),
                URLEncoder.encode(userDto.getHouseType(), StandardCharsets.UTF_8),
                userDto.getHasGarden(),
                userDto.getHasKids(),
                userDto.getHasOtherPets(),
                URLEncoder.encode(userDto.getCif(), StandardCharsets.UTF_8),
                URLEncoder.encode(userDto.getWebsite(), StandardCharsets.UTF_8)
        );

        String confirmationUrl = baseUrl + queryParams;
        String htmlContent = "<div style=\"font-family: 'Segoe UI', Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 25px; background: linear-gradient(to bottom, #ffffff, #fffaf5); border-radius: 12px; box-shadow: 0 5px 15px rgba(0,0,0,0.05);\">" +

                "<div style=\"text-align: center; margin-bottom: 35px; padding-bottom: 20px; border-bottom: 2px solid #fff0e6;\">" +
                "<div style=\"font-size: 40px; color: #ff8904; margin-bottom: 10px;\">🐕‍🦺</div>" +
                "<h1 style=\"color: #333; margin-bottom: 8px; font-size: 26px;\">¡Gracias por ser parte del cambio!</h1>" +
                "<p style=\"color: #ff8904; font-weight: 600; font-size: 16px;\">Plataforma de Adopción Responsable WOOF</p>" +
                "</div>" +

                "<p style=\"font-size: 16px; line-height: 1.7; color: #444; margin-bottom: 20px;\">" +
                "Estimado/a amigo/a de los animales,<br><br>" +
                "Recibimos tu solicitud de registro en <strong style=\"color: #ff8904;\">WOOF</strong>, la plataforma dedicada a salvar vidas y encontrar hogares responsables para animales en situación de abandono o maltrato." +
                "</p>" +

                "<div style=\"background: #fff8f0; border-radius: 10px; padding: 20px; margin: 25px 0; border: 1px solid #ffe4cc;\">" +
                "<p style=\"margin: 0 0 15px 0; font-size: 16px; color: #333; font-weight: 600;\">" +
                "🌍 Tu impacto en nuestra comunidad:" +
                "</p>" +
                "<ul style=\"margin: 0; padding-left: 20px; color: #555;\">" +
                "<li style=\"margin-bottom: 8px;\"><strong>Como adoptante:</strong> Encontrarás a tu compañero perfecto entre animales rescatados que buscan una segunda oportunidad</li>" +
                "<li style=\"margin-bottom: 8px;\"><strong>Como protector/rescatista:</strong> Podrás publicar animales en adopción con su historial médico y necesidades especiales</li>" +
                "<li><strong>Como voluntario:</strong> Te conectarás con refugios y organizaciones que necesitan ayuda</li>" +
                "</ul>" +
                "</div>" +

                "<div style=\"text-align: center; margin: 40px 0; padding: 25px; background: #fff0e6; border-radius: 12px;\">" +
                "<p style=\"color: #d45a00; font-weight: 600; margin-bottom: 15px; font-size: 17px;\">" +
                "¡Activa tu cuenta para empezar a salvar vidas!" +
                "</p>" +
                "<a href=\"" + confirmationUrl + "\" style=\"" +
                "display: inline-block; padding: 16px 40px; " +
                "background: linear-gradient(135deg, #ff8904, #ff6b00); " +
                "color: white; font-size: 18px; font-weight: bold; " +
                "text-decoration: none; border-radius: 8px; " +
                "box-shadow: 0 6px 20px rgba(255, 137, 4, 0.4); " +
                "border: none; cursor: pointer; " +
                "transition: all 0.3s ease; transform: translateY(0);\" " +
                "onmouseover=\"this.style.transform='translateY(-2px)'; this.style.boxShadow='0 8px 25px rgba(255, 137, 4, 0.5)'\" " +
                "onmouseout=\"this.style.transform='translateY(0)'; this.style.boxShadow='0 6px 20px rgba(255, 137, 4, 0.4)'\">" +
                "✅ Activar mi cuenta WOOF" +
                "</a>" +
                "</div>" +

                "<div style=\"background: #f0f9ff; border-radius: 10px; padding: 20px; margin: 25px 0; border-left: 4px solid #4da6ff;\">" +
                "<p style=\"margin: 0 0 10px 0; font-size: 14px; color: #0066cc; font-weight: 600;\">" +
                "ℹ️ Información importante:" +
                "</p>" +
                "<p style=\"margin: 0; font-size: 14px; color: #555; line-height: 1.6;\">" +
                "• Este enlace de confirmación expira en <strong>48 horas</strong><br>" +
                "• Trabajamos con refugios y protectoras verificadas<br>" +
                "• Promovemos la adopción responsable y el seguimiento post-adopción<br>" +
                "• Si tienes dudas, consulta nuestra guía de adopción responsable" +
                "</p>" +
                "</div>" +

                "<p style=\"font-size: 14px; color: #777; line-height: 1.6; margin-top: 25px;\">" +
                "<strong>Enlace alternativo:</strong> Si el botón no funciona, copia y pega esta URL en tu navegador:<br>" +
                "<span style=\"color: #ff8904; background: #fff8f0; padding: 8px 12px; border-radius: 6px; font-size: 13px; display: inline-block; margin-top: 8px; word-break: break-all;\">" + confirmationUrl + "</span>" +
                "</p>" +

                "<p style=\"font-size: 13px; color: #999; line-height: 1.5; margin-top: 30px; padding-top: 20px; border-top: 1px solid #eee;\">" +
                "<strong>«Cada adopción no solo salva una vida, sino que abre espacio para que otro animal sea rescatado.»</strong><br><br>" +
                "Si no has solicitado registrarte en WOOF, por favor ignora este mensaje.<br>" +
                "Gracias por tu compromiso con el bienestar animal." +
                "</p>" +

                "<div style=\"text-align: center; margin-top: 30px; padding-top: 20px; border-top: 1px solid #eee;\">" +
                "<p style=\"font-size: 12px; color: #aaa;\">" +
                "© " + java.time.Year.now().getValue() + " WOOF - Plataforma de Adopción Animal<br>" +
                "Email generado automáticamente. Por favor no responder." +
                "</p>" +
                "</div>" +
                "</div>";

        helper.setFrom("toninoflash89@gmail.com");
        helper.setTo(userDto.getEmail());
        helper.setSubject("Confirma tu registro");
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }
}
