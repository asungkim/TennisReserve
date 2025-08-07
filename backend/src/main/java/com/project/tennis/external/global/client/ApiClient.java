package com.project.tennis.external.global.client;

import org.springframework.web.client.RestClient;

public abstract class ApiClient {

    protected final RestClient restClient;

    public ApiClient(String basUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(basUrl)
                .build();
    }
}
