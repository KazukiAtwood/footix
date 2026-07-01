package com.footix.backend.service;

import com.footix.backend.dto.PlayerDto;
import com.footix.backend.entity.Joueur;
import com.footix.backend.repository.JoueurRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class PlayerService {

    private final JoueurRepository joueurRepository;
    private final TeamService teamService;

    public PlayerService(JoueurRepository joueurRepository, TeamService teamService) {
        this.joueurRepository = joueurRepository;
        this.teamService = teamService;
    }

    @Transactional(readOnly = true)
    public Optional<PlayerDto> getPlayerById(Long id) {
        teamService.syncTeamsFromApi();
        return joueurRepository.findById(id).map(teamService::toPlayerDto);
    }
}
