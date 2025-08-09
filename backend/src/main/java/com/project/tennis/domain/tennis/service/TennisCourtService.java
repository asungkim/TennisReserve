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
import java.util.Optional;

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
        List<TennisCourtResponse> result = new ArrayList<>();

        for (RawCourt court : courtList) {
            saveOne(court).ifPresent(result::add);
        }

        return result;
    }

    private Optional<TennisCourtResponse> saveOne(RawCourt raw) {
        // 1. 좌표 문제 없으면 추출
        Optional<Coordinate> opCoordinate = parseXY(raw.X(), raw.Y());
        if (opCoordinate.isEmpty()) {
            return Optional.empty();
        }
        Coordinate coordinate = opCoordinate.get();

        // 2. x,y를 정보를 통해 이미 저장되어있으면 재사용, 없으면 저장
        CourtLocation courtLocation = courtLocationRepository.findByXAndY(coordinate.x(), coordinate.y()).orElseGet(() -> makeCourtLocation(coordinate));

        // 3. 테니스 코트 이름 생성 및 중복 검사
        Optional<String> opCourtName = makeCourtName(raw.PLACENM());
        if (opCourtName.isEmpty()) {
            return Optional.empty();
        }
        String tennisCourtName = opCourtName.get();

        if (tennisCourtRepository.existsByName(tennisCourtName)) {
            return Optional.empty();
        }

        // 4. 테니스 코트 저장
        TennisCourt court = TennisCourt.builder()
                .name(tennisCourtName)
                .imageUrl(raw.IMGURL())
                .phoneNumber(raw.TELNO())
                .courtLocation(courtLocation)
                .build();

        TennisCourt savedTennisCourt = tennisCourtRepository.save(court);
        return Optional.of(TennisCourtResponse.from(savedTennisCourt));
    }

    private CourtLocation makeCourtLocation(Coordinate coordinate) {
        Double x = coordinate.x();
        Double y = coordinate.y();

        // x,y 기반 정보가 없으면 저장 x
        if (courtLocationRepository.existsByXAndY(x, y)) {
            return null;
        }

        Document document = kakaoLocationService.saveCourtLocation(x, y);
        Address address = document.address();
        RoadAddress roadAddress = document.roadAddress();

        String fullAddress = address != null ? address.addressName() : null;
        String recentAddress = roadAddress != null ? roadAddress.addressName() : null;
        String region1 = address != null ? address.region1depthName() : null;
        String region2 = address != null ? address.region2depthName() : null;
        String region3 = address != null ? address.region3depthName() : null;

        CourtLocation courtLocation = CourtLocation.builder()
                .fullAddress(fullAddress)
                .roadAddress(recentAddress)
                .region_one_depth(region1)
                .region_two_depth(region2)
                .region_three_depth(region3)
                .x(x)
                .y(y)
                .build();

        return courtLocationRepository.save(courtLocation);
    }

    private record Coordinate(Double x, Double y) {
    }

    private Optional<Coordinate> parseXY(String x, String y) {
        if (x == null || x.isBlank() || y == null || y.isBlank()) {
            return Optional.empty();
        }
        try {
            return Optional.of(new Coordinate(Double.valueOf(x), Double.valueOf(y)));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    private Optional<String> makeCourtName(String placeNm) {
        if (placeNm == null) return Optional.empty();
        if (placeNm.matches(".*[>/\\\\()\\[\\]{}&*%$#@!~^_=+|\"':;?].*")) {
            return Optional.empty();
        }

        String trimmed = placeNm.trim();
        return Optional.of(trimmed.contains("테니스장") ? trimmed : trimmed + " 테니스장");
    }
}
