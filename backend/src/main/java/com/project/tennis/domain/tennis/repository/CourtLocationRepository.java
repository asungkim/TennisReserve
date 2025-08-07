package com.project.tennis.domain.tennis.repository;

import com.project.tennis.domain.tennis.court.entity.CourtLocation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourtLocationRepository extends JpaRepository<CourtLocation, Long> {
}
