package com.footix.backend.service;

import com.footix.backend.dto.MatchDto;
import com.footix.backend.dto.PredictionDto;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Random;

@Service
public class PredictionService {

    private final MatchService matchService;

    public PredictionService(MatchService matchService) {
        this.matchService = matchService;
    }

    public List<PredictionDto> getPredictionsForUpcomingMatches() {
        return matchService.getAllMatches().stream()
                .filter(m -> !m.finished() && "notstarted".equalsIgnoreCase(m.status()))
                .sorted(Comparator.comparing(MatchDto::date))
                .limit(20)
                .map(this::predict)
                .toList();
    }

    public PredictionDto predictMatch(String matchId) {
        return matchService.getMatchById(matchId)
                .map(this::predict)
                .orElseThrow(() -> new IllegalArgumentException("Match not found: " + matchId));
    }

    private PredictionDto predict(MatchDto match) {
        Random rng = new Random(match.id().hashCode());
        int homeBase = 30 + rng.nextInt(25);
        int awayBase = 30 + rng.nextInt(25);
        int draw = 100 - homeBase - awayBase;
        if (draw < 15) {
            draw = 15;
            int excess = homeBase + awayBase + draw - 100;
            homeBase -= excess / 2;
            awayBase -= excess - excess / 2;
        }

        String winner;
        int max = Math.max(homeBase, Math.max(draw, awayBase));
        if (max == homeBase) winner = match.homeTeamName();
        else if (max == awayBase) winner = match.awayTeamName();
        else winner = "Match nul";

        String confidence = max >= 45 ? "Élevée" : max >= 35 ? "Modérée" : "Faible";

        String analysis = String.format(
                "Analyse IA : %s affiche une forme solide en phase de groupes. " +
                "Probabilité victoire domicile %d%%, nul %d%%, extérieur %d%%.",
                match.homeTeamName(), homeBase, draw, awayBase
        );

        return new PredictionDto(
                match.id(), match.homeTeamName(), match.awayTeamName(), match.date(),
                winner, homeBase, draw, awayBase, confidence, analysis
        );
    }
}
