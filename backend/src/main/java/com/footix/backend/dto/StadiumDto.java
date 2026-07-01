package com.footix.backend.dto;

public record StadiumDto(
        String id,
        String name,
        String city,
        String country,
        int capacity
) {}
