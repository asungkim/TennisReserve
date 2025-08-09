package com.project.tennis.external.tennis.client;

import com.project.tennis.external.global.client.ApiClient;
import com.project.tennis.external.tennis.dto.RawTennisApiResponse;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriBuilder;

@Component
public class TennisApiClient extends ApiClient {

    @Value("${custom.nuri.apikey}")
    private String apiKey;

    public TennisApiClient() {
        super("http://openAPI.seoul.go.kr:8088");
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
