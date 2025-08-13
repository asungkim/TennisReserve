package com.project.tennis.domain.tennis.court.repository;

import com.project.tennis.domain.tennis.court.entity.Court;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourtRepository extends JpaRepository<Court, Long> {
}
