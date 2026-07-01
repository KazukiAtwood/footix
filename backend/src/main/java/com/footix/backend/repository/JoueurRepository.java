package com.footix.backend.repository;

import com.footix.backend.entity.Joueur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JoueurRepository extends JpaRepository<Joueur, Long> {
    List<Joueur> findByEquipe_ApiId(String apiId);
    Optional<Joueur> findByApiId(String apiId);
}
