package com.footix.backend.service;

import tools.jackson.databind.JsonNode;
import com.footix.backend.dto.MatchDto;
import com.footix.backend.dto.MatchDetailDto;
import com.footix.backend.dto.PlayerDto;
import com.footix.backend.dto.TeamMatchStatsDto;
import com.footix.backend.dto.StadiumDto;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class MatchService {

    private static final Map<String, String> PHASE_LABELS = Map.of(
            "group", "Phase de groupes",
            "r32", "Seizièmes de finale",
            "r16", "Huitièmes de finale",
            "qf", "Quarts de finale",
            "sf", "Demi-finales",
            "third", "Petite finale",
            "final", "Finale"
    );

    private final WorldCupApiClient apiClient;
    private final StadiumService stadiumService;
    private final TeamService teamService;

    public MatchService(WorldCupApiClient apiClient, StadiumService stadiumService, TeamService teamService) {
        this.apiClient = apiClient;
        this.stadiumService = stadiumService;
        this.teamService = teamService;
    }

    public List<MatchDto> getAllMatches() {
        return parseGames(apiClient.get("/get/games"));
    }

    public List<MatchDto> getMatchesByPhase(String phase) {
        return getAllMatches().stream()
                .filter(m -> matchesPhase(m, phase))
                .sorted(Comparator.comparing(MatchDto::date))
                .toList();
    }

    public Optional<MatchDto> getMatchById(String id) {
        if (id == null || !id.matches("\\d+")) return Optional.empty();
        return getAllMatches().stream()
                .filter(m -> m.id().equals(id))
                .findFirst();
    }

    public Optional<MatchDetailDto> getMatchDetail(String id) {
        if (id == null || !id.matches("\\d+")) return Optional.empty();
        JsonNode rawGame = findRawGame(id);
        if (rawGame == null) return Optional.empty();

        MatchDto match = mapGame(rawGame);
        String homeFormation = teamService.getFormation(match.homeTeamId()).orElse("4-3-3");
        String awayFormation = teamService.getFormation(match.awayTeamId()).orElse("4-3-3");

        var homeStarters = hasRealTeam(match.homeTeamId())
                ? teamService.getStarters(match.homeTeamId()) : List.<PlayerDto>of();
        var homeBench = hasRealTeam(match.homeTeamId())
                ? teamService.getBench(match.homeTeamId()) : List.<PlayerDto>of();
        var awayStarters = hasRealTeam(match.awayTeamId())
                ? teamService.getStarters(match.awayTeamId()) : List.<PlayerDto>of();
        var awayBench = hasRealTeam(match.awayTeamId())
                ? teamService.getBench(match.awayTeamId()) : List.<PlayerDto>of();

        TeamMatchStatsDto homeStats = MatchEventParser.buildTeamStats(match.homeTeamName(), rawGame, true);
        TeamMatchStatsDto awayStats = MatchEventParser.buildTeamStats(match.awayTeamName(), rawGame, false);

        return Optional.of(new MatchDetailDto(match, homeFormation, awayFormation,
                homeStarters, homeBench, awayStarters, awayBench,
                homeStats, awayStats, unavailableStatistics()));
    }

    private JsonNode findRawGame(String id) {
        JsonNode response = apiClient.get("/get/games");
        if (response == null || !response.has("games")) return null;
        for (JsonNode g : response.get("games")) {
            if (id.equals(g.path("id").asText())) return g;
        }
        return null;
    }

    private static List<String> unavailableStatistics() {
        return List.of(
                "Cartons jaunes / rouges",
                "Tirs cadrés / non cadrés",
                "Passes",
                "Corners",
                "Possession de balle",
                "Coups francs"
        );
    }

    private boolean hasRealTeam(String teamId) {
        return teamId != null && !teamId.isBlank() && !"0".equals(teamId);
    }

    public boolean isLive(MatchDto match) {
        return !match.finished() && !"notstarted".equalsIgnoreCase(match.status());
    }

    private boolean matchesPhase(MatchDto m, String phase) {
        if (phase == null || phase.isBlank() || "all".equalsIgnoreCase(phase)) return true;
        return switch (phase.toLowerCase()) {
            case "group", "groups", "groupes" -> "group".equals(m.type());
            case "r32", "seiziemes" -> "r32".equals(m.type());
            case "r16", "huitiemes" -> "r16".equals(m.type());
            case "qf", "quarts" -> "qf".equals(m.type());
            case "sf", "demi" -> "sf".equals(m.type());
            case "third", "petite" -> "third".equals(m.type());
            case "final", "finale" -> "final".equals(m.type());
            case "knockout", "elimination" -> !"group".equals(m.type());
            default -> m.type().equalsIgnoreCase(phase);
        };
    }

    private List<MatchDto> parseGames(JsonNode response) {
        if (response == null || !response.has("games")) return List.of();
        JsonNode games = response.get("games");
        if (!games.isArray()) return List.of();
        return StreamSupport.stream(games.spliterator(), false)
                .map(this::mapGame)
                .toList();
    }

    MatchDto mapGame(JsonNode g) {
        String stadiumId = textOrEmpty(g, "stadium_id");
        StadiumDto stadium = stadiumService.getStadiumById(stadiumId).orElse(null);

        String homeName = textOrEmpty(g, "home_team_name_en");
        String awayName = textOrEmpty(g, "away_team_name_en");
        if (homeName.isEmpty()) homeName = textOrEmpty(g, "home_team_label");
        if (awayName.isEmpty()) awayName = textOrEmpty(g, "away_team_label");

        String type = textOrEmpty(g, "type");
        boolean finished = "TRUE".equalsIgnoreCase(textOrEmpty(g, "finished"));

        return new MatchDto(
                textOrEmpty(g, "id"),
                textOrEmpty(g, "home_team_id"),
                textOrEmpty(g, "away_team_id"),
                homeName,
                awayName,
                textOrEmpty(g, "home_team_label"),
                textOrEmpty(g, "away_team_label"),
                textOrEmpty(g, "home_score"),
                textOrEmpty(g, "away_score"),
                textOrEmpty(g, "home_scorers"),
                textOrEmpty(g, "away_scorers"),
                textOrEmpty(g, "group"),
                textOrEmpty(g, "matchday"),
                textOrEmpty(g, "local_date"),
                stadiumId,
                stadium != null ? stadium.name() : "",
                stadium != null ? stadium.city() : "",
                finished,
                textOrEmpty(g, "time_elapsed"),
                type,
                PHASE_LABELS.getOrDefault(type, type),
                textOrEmpty(g, "home_penalty_score"),
                textOrEmpty(g, "away_penalty_score")
        );
    }

    private String textOrEmpty(JsonNode node, String field) {
        if (node == null || !node.has(field) || node.get(field).isNull()) return "";
        return node.get(field).asText("");
    }
}
