package com.footix.backend.repository;

import com.footix.backend.entity.FavoriLocal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FavoriLocalRepository extends JpaRepository<FavoriLocal, Long> {
    Optional<FavoriLocal> findByMatchApiId(String matchApiId);
    void deleteByMatchApiId(String matchApiId);
}
