package com.footix.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "equipe")
public class Equipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEquipe;

    @Column(nullable = false, unique = true)
    private String apiId;

    @Column(nullable = false)
    private String nom;

    private String pays;

    private String fifaCode;

    private String groupe;

    private String flag;

    public Long getIdEquipe() { return idEquipe; }
    public void setIdEquipe(Long idEquipe) { this.idEquipe = idEquipe; }
    public String getApiId() { return apiId; }
    public void setApiId(String apiId) { this.apiId = apiId; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPays() { return pays; }
    public void setPays(String pays) { this.pays = pays; }
    public String getFifaCode() { return fifaCode; }
    public void setFifaCode(String fifaCode) { this.fifaCode = fifaCode; }
    public String getGroupe() { return groupe; }
    public void setGroupe(String groupe) { this.groupe = groupe; }
    public String getFlag() { return flag; }
    public void setFlag(String flag) { this.flag = flag; }
}
