package com.pet.businessdomain.petservice.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class LocationServiceImp implements ILocationService{


    @Value("${google.api.key}")
    private String googleApiKey;

    private final RestTemplate restTemplate;

    public LocationServiceImp(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    public double distanceBetweenAddresses(String address1, String address2) {
        try {
            Map<String, Double> coords1 = fetchCoordinates(address1);
            Map<String, Double> coords2 = fetchCoordinates(address2);

            return calculateDistanceKm(
                    coords1.get("lat"),
                    coords1.get("lng"),
                    coords2.get("lat"),
                    coords2.get("lng")
            );
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /* ========= MÉTODOS PRIVADOS ========= */

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

    private String normalizeSpanishAddress(String address) {
        return address
                .replaceAll("(?i)(^|\\s)(C\\.|C/|Cl\\.|Cl)(?=\\s)", " Calle ")
                .replaceAll("(?i)(^|\\s)(Av\\.|Avda|Avenida)(?=\\s)", " Avenida ")
                .replace(",", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }
}
