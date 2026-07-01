package com.footix.backend.repository;

import com.footix.backend.entity.Equipe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EquipeRepository extends JpaRepository<Equipe, Long> {
    Optional<Equipe> findByApiId(String apiId);
}
