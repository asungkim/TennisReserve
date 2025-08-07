package com.project.tennis.external.kakao.dto;

import java.util.List;

public record KakaoAddressResponse(
        Meta meta,
        List<Document> documents
) {
}
