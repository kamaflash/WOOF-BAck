package com.pet.businessdomain.petservice.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/location")
public class LocationController {

    @Value("${google.api.key}")
    private String googleApiKey;

    private final RestTemplate restTemplate;

    public LocationController(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    // --- Método interno para obtener coordenadas ---
    private Map<String, Double> fetchCoordinates(String address) throws Exception {

        String cleanAddress = normalizeSpanishAddress(address);

        String url = "https://maps.googleapis.com/maps/api/geocode/json?address=" +
                cleanAddress +
                "&components=country:ES" +
                "&key=" + googleApiKey;

        System.out.println("ADDRESS ENVIADA → " + cleanAddress);
        System.out.println("URL FINAL → " + url);

        Map body = restTemplate.getForObject(url, Map.class);

        if (!"OK".equals(body.get("status"))) {
            throw new Exception("Error Google Geocode API: " + body.get("status"));
        }

        List results = (List) body.get("results");

        if (results == null || results.isEmpty()) {
            throw new Exception("Google Geocode: ZERO_RESULTS para " + cleanAddress);
        }

        Map geometry = (Map)((Map)((List)body.get("results")).get(0)).get("geometry");
        return (Map<String, Double>) geometry.get("location");
    }
    private String normalizeSpanishAddress(String address) {

        return address
                // CALLE
                .replaceAll("(?i)(^|\\s)(C\\.|C/|C\\\\|Cl\\.|Cl)(?=\\s)", " Calle ")
                .replaceAll("(?i)(^|\\s)(Calle)(?=\\s)", " Calle ")
                // AVENIDA
                .replaceAll("(?i)(^|\\s)(Av\\.|Avda\\.|Avda|Avenida|Av)(?=\\s)", " Avenida ")
                // PLAZA
                .replaceAll("(?i)(^|\\s)(Pza\\.|Plaza|Pl\\.|Pl)(?=\\s)", " Plaza ")
                // PASEO
                .replaceAll("(?i)(^|\\s)(Pso\\.|Paseo|Pg\\.|Pg)(?=\\s)", " Paseo ")
                // CAMINO
                .replaceAll("(?i)(^|\\s)(Cno\\.|Camino|Cami)(?=\\s)", " Camino ")
                // CARRETERA
                .replaceAll("(?i)(^|\\s)(Ctra\\.|Carretera|Crta)(?=\\s)", " Carretera ")
                // RAMBLA
                .replaceAll("(?i)(^|\\s)(Rbla\\.|Rambla)(?=\\s)", " Rambla ")
                // URBANIZACIÓN
                .replaceAll("(?i)(^|\\s)(Urb\\.|Urbanizacion|Urbanización|Urb)(?=\\s)", " Urbanización ")
                // TRAVESÍA
                .replaceAll("(?i)(^|\\s)(Trv\\.|Travesia|Travesía)(?=\\s)", " Travesía ")
                // NÚMEROS Y EXTRA
                .replace(",", " ")
                .replace("º", "")
                .replace("ª", "")
                .replace("#", "")
                .replaceAll("\\s+", " ")
                .trim();
    }

    // --- Endpoint público: geocode ---
    @GetMapping("/geocode")
    public ResponseEntity<?> getCoordinates(@RequestParam("address") String address) {
        try {
            Map<String, Double> location = fetchCoordinates(address);
            return ResponseEntity.ok(location);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // --- Endpoint público: distance ---
    @GetMapping("/distance")
    public ResponseEntity<?> getDistance(
            @RequestParam(name = "address1") String address1,
            @RequestParam(name = "address2") String address2
    ) {
        try {
            Map<String, Double> coords1 = fetchCoordinates(address1);
            Map<String, Double> coords2 = fetchCoordinates(address2);

            double lat1 = coords1.get("lat");
            double lng1 = coords1.get("lng");
            double lat2 = coords2.get("lat");
            double lng2 = coords2.get("lng");

            double distance = calculateDistanceKm(lat1, lng1, lat2, lng2);
            return ResponseEntity.ok(distance);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // --- Haversine ---
    private double calculateDistanceKm(double lat1, double lng1, double lat2, double lng2) {
        double R = 6371; // radio Tierra en km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c;

        return Math.round(distance);
    }
}
