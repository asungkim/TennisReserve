package com.project.tennis.domain.tennis.repository;

import com.project.tennis.domain.tennis.court.entity.CourtLocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CourtLocationRepository extends JpaRepository<CourtLocation, Long> {
    boolean existsByXAndY(Double x, Double y);

    Optional<CourtLocation> findByXAndY(Double x, Double y);
}
