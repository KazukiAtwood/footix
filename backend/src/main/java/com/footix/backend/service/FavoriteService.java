package com.footix.backend.service;

import com.footix.backend.dto.FavoriDto;
import com.footix.backend.entity.FavoriLocal;
import com.footix.backend.repository.FavoriLocalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FavoriteService {

    private final FavoriLocalRepository repository;

    public FavoriteService(FavoriLocalRepository repository) {
        this.repository = repository;
    }

    public List<FavoriDto> getAll() {
        return repository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional
    public FavoriDto add(String matchId, String homeTeam, String awayTeam, String matchDate) {
        if (matchId == null || !matchId.matches("\\d+")) {
            throw new IllegalArgumentException("Invalid match ID");
        }
        return repository.findByMatchApiId(matchId)
                .map(this::toDto)
                .orElseGet(() -> {
                    FavoriLocal f = new FavoriLocal();
                    f.setMatchApiId(matchId);
                    f.setHomeTeam(sanitize(homeTeam));
                    f.setAwayTeam(sanitize(awayTeam));
                    f.setMatchDate(sanitize(matchDate));
                    return toDto(repository.save(f));
                });
    }

    @Transactional
    public void remove(String matchId) {
        repository.deleteByMatchApiId(matchId);
    }

    public boolean isFavorite(String matchId) {
        return repository.findByMatchApiId(matchId).isPresent();
    }

    private FavoriDto toDto(FavoriLocal f) {
        return new FavoriDto(f.getIdFavorisLocaux(), f.getMatchApiId(),
                f.getHomeTeam(), f.getAwayTeam(), f.getMatchDate());
    }

  private String sanitize(String input) {
    if (input == null) return "";
    String cleaned = input.replaceAll("[<>\"']", "").trim();
    return cleaned.substring(0, Math.min(cleaned.length(), 200));
  }
}
