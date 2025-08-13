package com.project.tennis.domain.tennis.tenniscourt.repository;

import com.project.tennis.domain.tennis.tenniscourt.entity.TennisCourtLocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CourtLocationRepository extends JpaRepository<TennisCourtLocation, Long> {
    boolean existsByXAndY(Double x, Double y);

    Optional<TennisCourtLocation> findByXAndY(Double x, Double y);
}
