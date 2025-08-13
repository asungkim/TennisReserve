package com.project.tennis.external.kakao.client;

import com.project.tennis.external.kakao.dto.KakaoAddressResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class KakaoApiClientTest {

    @Autowired
    private KakaoApiClient apiClient;

    @Test
    void fetch_success() {
        double x = 126.93256309656527;
        double y = 37.520599124440054;

        KakaoAddressResponse response = apiClient.getAddressByXY(x, y);

        assertThat(response).isNotNull();
        assertThat(response.meta().totalCount()).isEqualTo(1);
        assertThat(response.documents().size()).isEqualTo(1);
        assertThat(response.documents().get(0)).isNotNull();
    }
}