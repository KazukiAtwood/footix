package com.footix.backend.dto;

public record PredictionDto(
        String matchId,
        String homeTeam,
        String awayTeam,
        String date,
        String predictedWinner,
        int homeWinProbability,
        int drawProbability,
        int awayWinProbability,
        String confidence,
        String analysis
) {}
