package com.footix.backend.dto;

import java.util.List;

public record TeamDto(
        String id,
        String name,
        String fifaCode,
        String group,
        String flag,
        String country,
        String formation,
        List<PlayerDto> starters,
        List<PlayerDto> bench
) {}
