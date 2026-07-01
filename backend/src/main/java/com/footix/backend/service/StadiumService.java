package com.footix.backend.service;

import tools.jackson.databind.JsonNode;
import com.footix.backend.dto.StadiumDto;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.StreamSupport;

@Service
public class StadiumService {

    private final WorldCupApiClient apiClient;
    private Map<String, StadiumDto> cache;

    public StadiumService(WorldCupApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public Optional<StadiumDto> getStadiumById(String id) {
        if (id == null || id.isBlank()) return Optional.empty();
        ensureCache();
        return Optional.ofNullable(cache.get(id));
    }

    public List<StadiumDto> getAllStadiums() {
        ensureCache();
        return new ArrayList<>(cache.values());
    }

    private void ensureCache() {
        if (cache != null) return;
        cache = new HashMap<>();
        JsonNode response = apiClient.get("/get/stadiums");
        if (response == null) return;

        JsonNode stadiums = response.has("stadiums") ? response.get("stadiums") : response;
        if (stadiums.isArray()) {
            StreamSupport.stream(stadiums.spliterator(), false).forEach(s -> {
                StadiumDto dto = new StadiumDto(
                        s.path("id").asText(),
                        s.path("name_en").asText(),
                        s.path("city_en").asText(),
                        s.path("country_en").asText(),
                        s.path("capacity").asInt(0)
                );
                cache.put(dto.id(), dto);
            });
        }
    }
}
