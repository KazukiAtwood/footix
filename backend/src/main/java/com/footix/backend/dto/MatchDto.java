package com.footix.backend.dto;

import java.util.List;

public record MatchDto(
        String id,
        String homeTeamId,
        String awayTeamId,
        String homeTeamName,
        String awayTeamName,
        String homeTeamLabel,
        String awayTeamLabel,
        String homeScore,
        String awayScore,
        String homeScorers,
        String awayScorers,
        String group,
        String matchday,
        String date,
        String stadiumId,
        String stadiumName,
        String stadiumCity,
        boolean finished,
        String status,
        String type,
        String phaseLabel,
        String homePenaltyScore,
        String awayPenaltyScore
) {}
