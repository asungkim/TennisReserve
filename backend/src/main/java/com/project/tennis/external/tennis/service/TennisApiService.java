package com.project.tennis.external.tennis.service;

import com.project.tennis.external.tennis.client.TennisApiClient;
import com.project.tennis.external.tennis.dto.RawCourt;
import com.project.tennis.external.tennis.dto.RawResult;
import com.project.tennis.external.tennis.dto.RawTennisApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TennisApiService {

    private final TennisApiClient apiClient;

    public List<RawCourt> fetchCourtList() {
        RawTennisApiResponse rawResponse = apiClient.fetchCourtList("json", "ListPublicReservationSport", 1, 1000, "테니스장");

        // 1. 결과가 올바르지 않으면 저장 x
        RawResult result = rawResponse.list().RESULT();
        String successMessage = "INFO-000";
        if (!result.CODE().equals(successMessage)) {
            return null;
        }

        // 2. 결과가 정상이면 rawCourt 리스트 반환
        return rawResponse.list().row();
    }


}
