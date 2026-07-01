package com.footix.backend.dto;

public record PlayerDto(
        Long id,
        String name,
        String position,
        int number,
        int yellowCards,
        int redCards,
        String teamId,
        String teamName,
        String photoUrl,
        boolean starter,
        int goals,
        int assists,
        int age
) {}
