package com.project.tennis.domain.tennis.timeslot.repository;

import com.project.tennis.domain.tennis.timeslot.entity.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {
}
