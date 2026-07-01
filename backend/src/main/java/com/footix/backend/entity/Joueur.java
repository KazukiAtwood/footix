package com.footix.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "joueur")
public class Joueur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idJoueur;

    @Column(nullable = false)
    private String nom;

    private String poste;

    private String apiId;

    private int cartonRouge = 0;

    private int cartonJaune = 0;

    private int numeroMaillot;

    private boolean titulaire = false;

    private String photoUrl;

    private int buts = 0;

    private int passesDecisives = 0;

    private int age = 25;

    private String formationEquipe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipe_id")
    private Equipe equipe;

    public Long getIdJoueur() { return idJoueur; }
    public void setIdJoueur(Long idJoueur) { this.idJoueur = idJoueur; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPoste() { return poste; }
    public void setPoste(String poste) { this.poste = poste; }
    public String getApiId() { return apiId; }
    public void setApiId(String apiId) { this.apiId = apiId; }
    public int getCartonRouge() { return cartonRouge; }
    public void setCartonRouge(int cartonRouge) { this.cartonRouge = cartonRouge; }
    public int getCartonJaune() { return cartonJaune; }
    public void setCartonJaune(int cartonJaune) { this.cartonJaune = cartonJaune; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public boolean isTitulaire() { return titulaire; }
    public void setTitulaire(boolean titulaire) { this.titulaire = titulaire; }
    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
    public int getButs() { return buts; }
    public void setButs(int buts) { this.buts = buts; }
    public int getPassesDecisives() { return passesDecisives; }
    public void setPassesDecisives(int passesDecisives) { this.passesDecisives = passesDecisives; }
    public String getFormationEquipe() { return formationEquipe; }
    public void setFormationEquipe(String formationEquipe) { this.formationEquipe = formationEquipe; }
    public int getNumeroMaillot() { return numeroMaillot; }
    public void setNumeroMaillot(int numeroMaillot) { this.numeroMaillot = numeroMaillot; }
    public Equipe getEquipe() { return equipe; }
    public void setEquipe(Equipe equipe) { this.equipe = equipe; }
}
