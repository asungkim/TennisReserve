package com.project.tennis.domain.tennis.external;

import com.project.tennis.domain.tennis.external.dto.RawTennisApiResponse;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriBuilder;

@Component
public class TennisApiClient {

    private final RestClient restClient;

    @Value("${custom.apikey}")
    private String apiKey;

    public TennisApiClient() {
        this.restClient = RestClient.builder()
                .baseUrl("http://openAPI.seoul.go.kr:8088")
                .build();
    }

    public RawTennisApiResponse fetchCourtList(
            String type,
            String service,
            int startIdx,
            int endIdx,
            @Nullable String minClassNm) {
        return restClient.get()
                .uri(uriBuilder -> {
                    UriBuilder builder = uriBuilder
                            .pathSegment(apiKey, type, service
                                    , String.valueOf(startIdx), String.valueOf(endIdx));

                    if (minClassNm != null && !minClassNm.isBlank()) {
                        builder.queryParam("MINCLASSNM", minClassNm);
                    }

                    return builder.build();
                })
                .retrieve()
                .body(RawTennisApiResponse.class);
    }


}
