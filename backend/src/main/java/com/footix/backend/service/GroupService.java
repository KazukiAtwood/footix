package com.footix.backend.service;

import tools.jackson.databind.JsonNode;
import com.footix.backend.dto.GroupDto;
import com.footix.backend.dto.GroupStandingDto;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.StreamSupport;

@Service
public class GroupService {

    private final WorldCupApiClient apiClient;
    private final TeamService teamService;

    public GroupService(WorldCupApiClient apiClient, TeamService teamService) {
        this.apiClient = apiClient;
        this.teamService = teamService;
    }

    public List<GroupDto> getAllGroups() {
        teamService.syncTeamsFromApi();
        JsonNode response = apiClient.get("/get/groups");
        if (response == null) return List.of();

        JsonNode groups = response.has("groups") ? response.get("groups") : response;
        if (!groups.isArray()) return List.of();

        Map<String, String> teamNames = new HashMap<>();
        Map<String, String> teamFlags = new HashMap<>();
        teamService.getAllTeams().forEach(t -> {
            teamNames.put(t.id(), t.name());
            teamFlags.put(t.id(), t.flag());
        });

        return StreamSupport.stream(groups.spliterator(), false)
                .map(g -> {
                    String groupName = g.path("name").asText();
                    if (groupName.isEmpty()) groupName = g.path("group").asText();
                    List<GroupStandingDto> standings = new ArrayList<>();
                    JsonNode teams = g.path("teams");
                    if (teams.isArray()) {
                        StreamSupport.stream(teams.spliterator(), false)
                                .sorted((a, b) -> Integer.compare(
                                        b.path("pts").asInt(0),
                                        a.path("pts").asInt(0)))
                                .forEach(t -> {
                                    int gf = t.path("gf").asInt(0);
                                    int ga = t.path("ga").asInt(0);
                                    String tid = t.path("team_id").asText();
                                    standings.add(new GroupStandingDto(
                                            tid,
                                            teamNames.getOrDefault(tid, "Team " + tid),
                                            teamFlags.getOrDefault(tid, ""),
                                            t.path("pts").asInt(0),
                                            gf, ga, gf - ga
                                    ));
                                });
                    }
                    return new GroupDto(groupName, standings);
                })
                .sorted(Comparator.comparing(GroupDto::group))
                .toList();
    }
}
