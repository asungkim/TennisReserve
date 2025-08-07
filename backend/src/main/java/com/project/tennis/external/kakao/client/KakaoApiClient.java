package com.project.tennis.external.kakao.client;

import com.project.tennis.external.global.client.ApiClient;
import com.project.tennis.external.kakao.dto.KakaoAddressResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KakaoApiClient extends ApiClient {

    @Value("${custom.kakao.apiKey}")
    private String apiKey;

    public KakaoApiClient() {
        super("https://dapi.kakao.com");
    }

    public KakaoAddressResponse getAddressByXY(double x, double y) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v2/local/geo/coord2address.json")
                        .queryParam("x", x)
                        .queryParam("y", y)
                        .build())
                .header("Authorization", "KakaoAK " + apiKey)
                .retrieve()
                .body(KakaoAddressResponse.class);
    }
}
