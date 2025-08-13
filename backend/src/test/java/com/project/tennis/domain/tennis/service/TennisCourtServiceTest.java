package com.project.tennis.domain.tennis.service;

import com.project.tennis.domain.tennis.tenniscourt.service.TennisCourtService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TennisCourtServiceTest {

    @InjectMocks
    private TennisCourtService tennisCourtService;
}