package com.footix.backend.dto;

public record FavoriDto(
        Long id,
        String matchId,
        String homeTeam,
        String awayTeam,
        String matchDate
) {}
