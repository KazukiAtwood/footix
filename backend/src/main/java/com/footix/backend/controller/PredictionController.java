package com.footix.backend.controller;

import com.footix.backend.dto.PredictionDto;
import com.footix.backend.service.PredictionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/predictions")
public class PredictionController {

    private final PredictionService predictionService;

    public PredictionController(PredictionService predictionService) {
        this.predictionService = predictionService;
    }

    @GetMapping
    public List<PredictionDto> getPredictions() {
        return predictionService.getPredictionsForUpcomingMatches();
    }

    @GetMapping("/{matchId}")
    public PredictionDto getPrediction(@PathVariable String matchId) {
        return predictionService.predictMatch(matchId);
    }
}
