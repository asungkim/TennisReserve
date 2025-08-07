package com.project.tennis.domain.tennis.service;

import com.project.tennis.domain.tennis.court.dto.TennisCourtResponse;
import com.project.tennis.domain.tennis.court.entity.CourtLocation;
import com.project.tennis.domain.tennis.court.entity.TennisCourt;
import com.project.tennis.domain.tennis.repository.CourtLocationRepository;
import com.project.tennis.domain.tennis.repository.TennisCourtRepository;
import com.project.tennis.external.kakao.dto.Address;
import com.project.tennis.external.kakao.dto.Document;
import com.project.tennis.external.kakao.dto.RoadAddress;
import com.project.tennis.external.kakao.service.KakaoLocationService;
import com.project.tennis.external.tennis.dto.RawCourt;
import com.project.tennis.external.tennis.service.TennisApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TennisCourtService {

    private final TennisApiService tennisApiService;
    private final KakaoLocationService kakaoLocationService;
    private final TennisCourtRepository tennisCourtRepository;
    private final CourtLocationRepository courtLocationRepository;

    public List<TennisCourtResponse> saveCourtAndLocationByExternalApi() {
        List<RawCourt> courtList = tennisApiService.fetchCourtList();

        List<TennisCourtResponse> list = new ArrayList<>();

        for (RawCourt court : courtList) {
            Double x = Double.valueOf(court.X());
            Double y = Double.valueOf(court.Y());

            Document document = kakaoLocationService.saveCourtLocation(x, y);
            Address address = document.address();
            RoadAddress roadAddress = document.roadAddress();

            String fullAddress = address != null ? address.addressName() : null;
            String recentAddress = roadAddress != null ? roadAddress.addressName() : null;
            String region1 = address != null ? address.region1depthName() : null;
            String region2 = address != null ? address.region2depthName() : null;
            String region3 = address != null ? address.region3depthName() : null;

            // 1. CourtLocation 저장
            CourtLocation courtLocation = CourtLocation.builder()
                    .fullAddress(fullAddress)
                    .roadAddress(recentAddress)
                    .region_one_depth(region1)
                    .region_two_depth(region2)
                    .region_three_depth(region3)
                    .x(x)
                    .y(y)
                    .build();

            courtLocationRepository.save(courtLocation);

            // 2. TennisCourt 저장
            String tennisCourtName = makeNameByData(court.PLACENM());
            TennisCourt tennisCourt = TennisCourt.builder()
                    .name(tennisCourtName)
                    .imageUrl(court.IMGURL())
                    .phoneNumber(court.TELNO())
                    .courtLocation(courtLocation)
                    .build();

            tennisCourtRepository.save(tennisCourt);

            // 3. DTO 변환
            TennisCourtResponse response = TennisCourtResponse.from(tennisCourt);
            list.add(response);
        }

        return list;
    }

    private String makeNameByData(String placeNm) {
        boolean isPlaceNameInvalid = placeNm.matches(".*[>/\\\\].*");

        // PLACENM이 유효하면 그대로 사용
        if (!isPlaceNameInvalid) {
            if (placeNm.contains("테니스장")) {
                return placeNm.trim();
            } else {
                return placeNm.trim() + " 테니스장";
            }
        }

        return null;
    }
}
