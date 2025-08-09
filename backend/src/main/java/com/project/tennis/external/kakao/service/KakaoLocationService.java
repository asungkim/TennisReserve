package com.project.tennis.external.kakao.service;

import com.project.tennis.external.kakao.client.KakaoApiClient;
import com.project.tennis.external.kakao.dto.Document;
import com.project.tennis.external.kakao.dto.KakaoAddressResponse;
import com.project.tennis.global.exception.BusinessException;
import com.project.tennis.global.response.RsCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class KakaoLocationService {

    private final KakaoApiClient apiClient;

    public Document saveCourtLocation(Double x, Double y) {
        KakaoAddressResponse response = apiClient.getAddressByXY(x, y);

        int count = response.meta().totalCount();
        if (count == 0) {
            throw new BusinessException(RsCode.NOT_FOUND);
        }

        return response.documents().get(0);
    }
}
