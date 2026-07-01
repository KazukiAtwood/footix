package com.footix.backend.dto;

public record GroupDto(
        String group,
        java.util.List<GroupStandingDto> standings
) {}
