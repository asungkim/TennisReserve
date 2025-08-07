package com.project.tennis.domain.tennis.repository;

import com.project.tennis.domain.tennis.court.entity.TennisCourt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TennisCourtRepository extends JpaRepository<TennisCourt, Long> {
}
