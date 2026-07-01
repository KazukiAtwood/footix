package com.footix.backend.service;

import com.footix.backend.dto.MatchEventDto;
import com.footix.backend.dto.TeamMatchStatsDto;
import tools.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class MatchEventParser {

    private MatchEventParser() {}

    public static List<MatchEventDto> parseGoals(String raw) {
        List<String> entries = parseList(raw);
        List<MatchEventDto> events = new ArrayList<>();
        for (String entry : entries) {
            String cleaned = entry.trim();
            if (cleaned.isBlank()) continue;
            String player = cleaned;
            String minute = "";
            Matcher m = Pattern.compile("^(.+?)\\s+(\\d+(?:\\+\\d+)?)'?\\s*$").matcher(cleaned);
            if (m.matches()) {
                player = m.group(1).trim();
                minute = m.group(2) + "'";
            }
            events.add(new MatchEventDto("GOAL", player, minute,
                    minute.isEmpty() ? player : player + " " + minute));
        }
        return events;
    }

    public static List<String> parseNames(String raw) {
        return parseList(raw);
    }

    static TeamMatchStatsDto buildTeamStats(String teamName, JsonNode game, boolean home) {
        String prefix = home ? "home" : "away";
        int goals = parseIntSafe(game.path(prefix + "_score").asText("0"));
        List<MatchEventDto> goalEvents = parseGoals(textOrEmpty(game, prefix + "_scorers"));
        List<String> penScorers = parseNames(textOrEmpty(game, prefix + "_penalty_scorers"));
        List<String> penMisses = parseNames(textOrEmpty(game, prefix + "_penalty_misses"));

        return new TeamMatchStatsDto(
                teamName,
                goals,
                goalEvents,
                penScorers.size(),
                penMisses.size(),
                penScorers,
                penMisses
        );
    }

    private static List<String> parseList(String raw) {
        if (raw == null || raw.isBlank() || "null".equalsIgnoreCase(raw.trim())) {
            return List.of();
        }
        List<String> result = new ArrayList<>();
        String s = raw.trim();
        if (s.startsWith("{") && s.endsWith("}")) {
            s = s.substring(1, s.length() - 1).trim();
        }
        if (s.isBlank()) return List.of();

        StringBuilder current = new StringBuilder();
        boolean inQuote = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '"') inQuote = !inQuote;
            else if (c == ',' && !inQuote) {
                addEntry(result, current.toString());
                current.setLength(0);
                continue;
            }
            current.append(c);
        }
        addEntry(result, current.toString());
        return result;
    }

    private static void addEntry(List<String> result, String part) {
        String cleaned = part.replaceAll("^[\"']+|[\"']+$", "").trim();
        if (!cleaned.isBlank() && !"null".equalsIgnoreCase(cleaned)) {
            result.add(cleaned);
        }
    }

    private static int parseIntSafe(String s) {
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static String textOrEmpty(JsonNode node, String field) {
        if (node == null || !node.has(field) || node.get(field).isNull()) return "";
        return node.get(field).asText("");
    }
}
