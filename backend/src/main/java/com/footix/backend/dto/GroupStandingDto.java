package com.footix.backend.dto;

public record GroupStandingDto(
        String teamId,
        String teamName,
        String flag,
        int points,
        int goalsFor,
        int goalsAgainst,
        int goalDifference
) {}
