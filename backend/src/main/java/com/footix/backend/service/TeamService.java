package com.footix.backend.service;

import tools.jackson.databind.JsonNode;
import com.footix.backend.dto.PlayerDto;
import com.footix.backend.dto.TeamDto;
import com.footix.backend.entity.Equipe;
import com.footix.backend.entity.Joueur;
import com.footix.backend.repository.EquipeRepository;
import com.footix.backend.repository.JoueurRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.StreamSupport;

@Service
public class TeamService {

    private final WorldCupApiClient apiClient;
    private final EquipeRepository equipeRepository;
    private final JoueurRepository joueurRepository;
    private final SquadProvider squadProvider;

    public TeamService(WorldCupApiClient apiClient, EquipeRepository equipeRepository,
                       JoueurRepository joueurRepository, SquadProvider squadProvider) {
        this.apiClient = apiClient;
        this.equipeRepository = equipeRepository;
        this.joueurRepository = joueurRepository;
        this.squadProvider = squadProvider;
    }

    public List<TeamDto> getAllTeams() {
        syncTeamsFromApi();
        return equipeRepository.findAll().stream()
                .map(e -> toDto(e, false))
                .sorted(Comparator.comparing(TeamDto::name))
                .toList();
    }

    public Optional<TeamDto> getTeamById(String apiId) {
        syncTeamsFromApi();
        return equipeRepository.findByApiId(apiId)
                .map(e -> toDto(e, true));
    }

    public List<TeamDto> getTeamsByGroup(String group) {
        syncTeamsFromApi();
        return equipeRepository.findAll().stream()
                .filter(e -> group.equalsIgnoreCase(e.getGroupe()))
                .map(e -> toDto(e, false))
                .toList();
    }

    public Optional<String> getFormation(String teamApiId) {
        syncTeamsFromApi();
        return joueurRepository.findByEquipe_ApiId(teamApiId).stream()
                .map(Joueur::getFormationEquipe)
                .filter(Objects::nonNull)
                .findFirst();
    }

    public List<PlayerDto> getStarters(String teamApiId) {
        return getPlayersByTeam(teamApiId).stream().filter(PlayerDto::starter).toList();
    }

    public List<PlayerDto> getBench(String teamApiId) {
        return getPlayersByTeam(teamApiId).stream().filter(p -> !p.starter()).toList();
    }

    public List<PlayerDto> getPlayersByTeam(String teamApiId) {
        syncTeamsFromApi();
        return joueurRepository.findByEquipe_ApiId(teamApiId).stream()
                .sorted(Comparator.comparing(Joueur::getNumeroMaillot))
                .map(this::toPlayerDto)
                .toList();
    }

    @Transactional
    public void syncTeamsFromApi() {
        if (equipeRepository.count() > 0) return;

        JsonNode response = apiClient.get("/get/teams");
        if (response == null) return;

        JsonNode teams = response.has("teams") ? response.get("teams") : response;
        if (!teams.isArray()) return;

        StreamSupport.stream(teams.spliterator(), false).forEach(t -> {
            Equipe equipe = new Equipe();
            equipe.setApiId(t.path("id").asText());
            equipe.setNom(t.path("name_en").asText());
            equipe.setPays(t.path("iso2").asText());
            equipe.setFifaCode(t.path("fifa_code").asText());
            equipe.setGroupe(t.path("groups").asText());
            equipe.setFlag(t.path("flag").asText());
            equipeRepository.save(equipe);
            generateSquadForTeam(equipe);
        });
    }

    private void generateSquadForTeam(Equipe equipe) {
        SquadProvider.SquadTemplate squad = squadProvider.getSquad(equipe.getNom(), equipe.getPays());
        Random rng = new Random(equipe.getApiId().hashCode());

        for (SquadProvider.PlayerSeed seed : squad.players()) {
            Joueur j = new Joueur();
            j.setApiId(equipe.getApiId() + "-" + seed.number());
            j.setNom(seed.name());
            j.setPoste(seed.position());
            j.setNumeroMaillot(seed.number());
            j.setTitulaire(seed.starter());
            j.setAge(seed.age());
            j.setPhotoUrl(SquadProvider.photoUrl(seed.name()));
            j.setFormationEquipe(squad.formation());
            j.setCartonJaune(seed.starter() ? rng.nextInt(3) : rng.nextInt(2));
            j.setCartonRouge(0);
            j.setButs(seed.starter() && "FW".equals(seed.position()) ? rng.nextInt(4) : rng.nextInt(2));
            j.setPassesDecisives(seed.starter() && "MF".equals(seed.position()) ? rng.nextInt(3) : rng.nextInt(2));
            j.setEquipe(equipe);
            joueurRepository.save(j);
        }
    }

    private TeamDto toDto(Equipe e, boolean withPlayers) {
        List<PlayerDto> all = withPlayers ? getPlayersByTeam(e.getApiId()) : List.of();
        String formation = joueurRepository.findByEquipe_ApiId(e.getApiId()).stream()
                .map(Joueur::getFormationEquipe)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse("4-3-3");

        return new TeamDto(
                e.getApiId(), e.getNom(), e.getFifaCode(), e.getGroupe(), e.getFlag(), e.getPays(),
                formation,
                all.stream().filter(PlayerDto::starter).toList(),
                all.stream().filter(p -> !p.starter()).toList()
        );
    }

    PlayerDto toPlayerDto(Joueur j) {
        return new PlayerDto(
                j.getIdJoueur(),
                j.getNom(),
                j.getPoste(),
                j.getNumeroMaillot(),
                j.getCartonJaune(),
                j.getCartonRouge(),
                j.getEquipe().getApiId(),
                j.getEquipe().getNom(),
                j.getPhotoUrl(),
                j.isTitulaire(),
                j.getButs(),
                j.getPassesDecisives(),
                j.getAge()
        );
    }
}
