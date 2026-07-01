package com.footix.backend.service;

import tools.jackson.databind.JsonNode;
import com.footix.backend.config.WorldCupApiProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class WorldCupApiClient {

    private static final Logger log = LoggerFactory.getLogger(WorldCupApiClient.class);

    private final WebClient webClient;
    private final WorldCupApiProperties properties;
    private final AtomicReference<String> token = new AtomicReference<>();

    public WorldCupApiClient(WebClient worldCupWebClient, WorldCupApiProperties properties) {
        this.webClient = worldCupWebClient;
        this.properties = properties;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void authenticateOnStartup() {
        if (properties.token() != null && !properties.token().isBlank()) {
            token.set(properties.token());
            log.info("World Cup API token loaded from configuration");
            return;
        }
        try {
            if (properties.email() != null && !properties.email().isBlank()) {
                login(properties.email(), properties.password());
            } else {
                registerServiceAccount();
            }
        } catch (Exception e) {
            log.warn("Could not authenticate with World Cup API: {}", e.getMessage());
        }
    }

    private void registerServiceAccount() {
        String email = "footix-" + UUID.randomUUID().toString().substring(0, 8) + "@footix.local";
        JsonNode response = webClient.post()
                .uri("/auth/register")
                .bodyValue(Map.of("name", "Footix", "email", email, "password", "FootixSecure2026!"))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();
        if (response != null && response.has("token")) {
            token.set(response.get("token").asText());
            log.info("Registered service account for World Cup API");
        }
    }

    private void login(String email, String password) {
        JsonNode response = webClient.post()
                .uri("/auth/authenticate")
                .bodyValue(Map.of("email", email, "password", password))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();
        if (response != null && response.has("token")) {
            token.set(response.get("token").asText());
            log.info("Authenticated with World Cup API");
        }
    }

    public JsonNode get(String path) {
        try {
            return webClient.get()
                    .uri(path)
                    .headers(h -> {
                        String t = token.get();
                        if (t != null) h.setBearerAuth(t);
                    })
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();
        } catch (WebClientResponseException.Unauthorized e) {
            authenticateOnStartup();
            return webClient.get()
                    .uri(path)
                    .headers(h -> {
                        String t = token.get();
                        if (t != null) h.setBearerAuth(t);
                    })
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();
        }
    }
}
