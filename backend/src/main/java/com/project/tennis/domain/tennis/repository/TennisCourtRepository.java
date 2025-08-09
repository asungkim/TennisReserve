package com.project.tennis.domain.tennis.repository;

import com.project.tennis.domain.tennis.court.entity.TennisCourt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TennisCourtRepository extends JpaRepository<TennisCourt, Long> {
    boolean existsByName(String name);

    Optional<TennisCourt> findByName(String name);
}
