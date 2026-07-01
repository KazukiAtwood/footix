package com.footix.backend.dto;

public record MatchEventDto(
        String type,
        String player,
        String minute,
        String label
) {}
