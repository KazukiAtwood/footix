package com.footix.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "worldcup.api")
public record WorldCupApiProperties(
        String baseUrl,
        String email,
        String password,
        String token
) {}
