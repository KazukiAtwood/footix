package com.footix.backend.dto;

import java.util.List;

public record MatchDetailDto(
        MatchDto match,
        String homeFormation,
        String awayFormation,
        List<PlayerDto> homeStarters,
        List<PlayerDto> homeBench,
        List<PlayerDto> awayStarters,
        List<PlayerDto> awayBench
) {}
