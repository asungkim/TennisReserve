package com.project.tennis.domain.tennis.external;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TennisApiService {

    private final TennisApiClient apiClient;

}
