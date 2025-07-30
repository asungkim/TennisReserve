package com.project.tennis.domain.tennis.external;

import com.project.tennis.domain.tennis.external.dto.RawTennisApiResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TennisApiClientTest {

    @Autowired
    private TennisApiClient apiClient;

    @Test
    void fetch_success() {
        String type = "json";
        String service = "ListPublicReservationSport";
        int startIdx = 1;
        int endIdx = 5;
        String minClassNm = "테니스장";

        RawTennisApiResponse response = apiClient.fetchCourtList(type, service, startIdx, endIdx, minClassNm);

        assertThat(response).isNotNull();
        assertThat(response.list().RESULT().CODE()).isEqualTo("INFO-000");
        assertThat(response.list().row().size()).isEqualTo(5);
    }
}