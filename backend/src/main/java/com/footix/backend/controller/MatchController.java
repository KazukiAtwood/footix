package com.footix.backend.controller;

import com.footix.backend.dto.MatchDto;
import com.footix.backend.service.FavoriteService;
import com.footix.backend.service.MatchService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/matches")
public class MatchController {

    private final MatchService matchService;
    private final FavoriteService favoriteService;

    @org.springframework.beans.factory.annotation.Value("${footix.poll-interval-ms:15000}")
    private long pollIntervalMs;

    public MatchController(MatchService matchService, FavoriteService favoriteService) {
        this.matchService = matchService;
        this.favoriteService = favoriteService;
    }

    @GetMapping
    public List<MatchDto> getMatches(@RequestParam(required = false) String phase) {
        if (phase != null && !phase.isBlank()) {
            return matchService.getMatchesByPhase(phase);
        }
        return matchService.getAllMatches();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMatch(@PathVariable String id, @RequestParam(defaultValue = "false") boolean detail) {
        if (detail) {
            return matchService.getMatchDetail(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        }
        return matchService.getMatchById(id)
                .map(m -> ResponseEntity.ok(enrichWithFavorite(m)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping(value = "/{id}/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamMatch(@PathVariable String id) {
        SseEmitter emitter = new SseEmitter(0L);
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate(() -> {
            try {
                matchService.getMatchById(id).ifPresentOrElse(match -> {
                    try {
                        emitter.send(SseEmitter.event()
                                .name("scoreEvent")
                                .data(enrichWithFavorite(match)));
                        if (match.finished()) {
                            emitter.complete();
                            scheduler.shutdown();
                        }
                    } catch (IOException e) {
                        emitter.completeWithError(e);
                        scheduler.shutdown();
                    }
                }, () -> {
                    emitter.completeWithError(new IllegalArgumentException("Match not found"));
                    scheduler.shutdown();
                });
            } catch (Exception e) {
                emitter.completeWithError(e);
                scheduler.shutdown();
            }
        }, 0, pollIntervalMs, TimeUnit.MILLISECONDS);

        emitter.onCompletion(scheduler::shutdown);
        emitter.onTimeout(scheduler::shutdown);
        emitter.onError(e -> scheduler.shutdown());

        return emitter;
    }

    private MatchDto enrichWithFavorite(MatchDto m) {
        return m;
    }

    @PostMapping("/{id}/favorite")
    public ResponseEntity<?> addFavorite(@PathVariable String id) {
        return matchService.getMatchById(id)
                .map(m -> ResponseEntity.ok(favoriteService.add(id, m.homeTeamName(), m.awayTeamName(), m.date())))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}/favorite")
    public ResponseEntity<Map<String, String>> removeFavorite(@PathVariable String id) {
        favoriteService.remove(id);
        return ResponseEntity.ok(Map.of("status", "removed"));
    }
}
