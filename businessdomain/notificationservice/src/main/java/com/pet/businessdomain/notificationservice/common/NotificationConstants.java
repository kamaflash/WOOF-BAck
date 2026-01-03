package com.pet.businessdomain.notificationservice.common;

import java.util.List;

public final class NotificationConstants {

    private NotificationConstants() {
        // Evita instanciación
    }

    public static final List<String> CATEGORIES = List.of(
            "Pintura",
            "Escultura",
            "Fotografía",
            "Dibujo",
            "Ilustración",
            "Grabado",
            "Arte digital",
            "Collage",
            "Cerámica"
    );

    public static final List<String> STATUSES = List.of(
            "draft",     // Borrador
            "published", // Publicado
            "review",    // En revisión
            "archived",  // Archivado
            "rejected",  // Rechazado
            "sold"       // Vendido
    );
}
