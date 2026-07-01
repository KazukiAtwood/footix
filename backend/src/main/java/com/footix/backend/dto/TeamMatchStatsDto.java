package com.footix.backend.dto;

import java.util.List;

public record TeamMatchStatsDto(
        String teamName,
        int goals,
        List<MatchEventDto> goalEvents,
        int penaltyShootoutScored,
        int penaltyShootoutMissed,
        List<String> penaltyScorers,
        List<String> penaltyMisses
) {}
