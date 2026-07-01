package com.footix.backend.controller;

import com.footix.backend.dto.FavoriDto;
import com.footix.backend.service.FavoriteService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @GetMapping
    public List<FavoriDto> getFavorites() {
        return favoriteService.getAll();
    }

    @DeleteMapping("/{matchId}")
    public void removeFavorite(@PathVariable String matchId) {
        favoriteService.remove(matchId);
    }
}
