package com.footix.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "favoris_locaux")
public class FavoriLocal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idFavorisLocaux;

    @Column(nullable = false, unique = true)
    private String matchApiId;

    private String homeTeam;
    private String awayTeam;
    private String matchDate;

    public Long getIdFavorisLocaux() { return idFavorisLocaux; }
    public void setIdFavorisLocaux(Long idFavorisLocaux) { this.idFavorisLocaux = idFavorisLocaux; }
    public String getMatchApiId() { return matchApiId; }
    public void setMatchApiId(String matchApiId) { this.matchApiId = matchApiId; }
    public String getHomeTeam() { return homeTeam; }
    public void setHomeTeam(String homeTeam) { this.homeTeam = homeTeam; }
    public String getAwayTeam() { return awayTeam; }
    public void setAwayTeam(String awayTeam) { this.awayTeam = awayTeam; }
    public String getMatchDate() { return matchDate; }
    public void setMatchDate(String matchDate) { this.matchDate = matchDate; }
}
