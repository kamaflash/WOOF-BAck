package com.pet.businessdomain.petservice.dto;

import lombok.Data;

import java.util.List;

@Data
public class DistanceMatrixDto {
    private List<Row> rows;

    @Data
    public static class Row {
        private List<Element> elements;
    }

    @Data
    public static class Element {
        private Distance distance;
        private String status;
    }

    @Data
    public static class Distance {
        private long value; // metros
    }
}
