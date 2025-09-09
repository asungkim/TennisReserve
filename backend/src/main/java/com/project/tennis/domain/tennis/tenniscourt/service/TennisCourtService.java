package com.project.tennis.domain.tennis.tenniscourt.service;

import com.project.tennis.domain.tennis.tenniscourt.dto.request.TennisCourtRequest;
import com.project.tennis.domain.tennis.tenniscourt.dto.response.TennisCourtResponse;
import com.project.tennis.domain.tennis.tenniscourt.entity.TennisCourt;
import com.project.tennis.domain.tennis.tenniscourt.entity.TennisCourtLocation;
import com.project.tennis.domain.tennis.tenniscourt.repository.CourtLocationRepository;
import com.project.tennis.domain.tennis.tenniscourt.repository.TennisCourtRepository;
import com.project.tennis.external.kakao.dto.Address;
import com.project.tennis.external.kakao.dto.Document;
import com.project.tennis.external.kakao.dto.RoadAddress;
import com.project.tennis.external.kakao.service.KakaoLocationService;
import com.project.tennis.external.tennis.dto.RawCourt;
import com.project.tennis.external.tennis.service.TennisApiService;
import com.project.tennis.global.exception.BusinessException;
import com.project.tennis.global.response.RsCode;
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

    public TennisCourtResponse createTennisCourt(TennisCourtRequest tennisCourtRequest) {

        validateTennisCourtName(tennisCourtRequest.name());

        TennisCourt tennisCourt = TennisCourt.builder()
                .name(tennisCourtRequest.name())
                .imageUrl(tennisCourtRequest.imageUrl())
                .phoneNumber(tennisCourtRequest.phoneNumber())
                .build();

        tennisCourtRepository.save(tennisCourt);

        return TennisCourtResponse.from(tennisCourt);
    }

    @Transactional(readOnly = true)
    public List<TennisCourtResponse> getTennisCourts() {
        return tennisCourtRepository.findAll()
                .stream()
                .map(TennisCourtResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public TennisCourtResponse getTennisCourt(Long id) {
        return TennisCourtResponse.from(findById(id));
    }

    public TennisCourtResponse modifyTennisCourt(TennisCourtRequest request, Long id) {
        TennisCourt tennisCourt = findById(id);
        tennisCourt.update(request.name(), request.imageUrl(), request.phoneNumber());
        return TennisCourtResponse.from(tennisCourt);
    }

    public void deleteTennisCourt(Long id) {
        tennisCourtRepository.delete(findById(id));
    }

    private TennisCourt findById(Long id) {
        return tennisCourtRepository.findById(id)
                .orElseThrow(() -> new BusinessException(RsCode.NOT_FOUND));
    }

    private void validateTennisCourtName(String name) {
        boolean isExist = tennisCourtRepository.existsByName(name);

        if (isExist) {
            throw new BusinessException(RsCode.DUPLICATE_TENNIS_COURT_NAME);
        }
    }

    private Optional<TennisCourtResponse> saveOne(RawCourt raw) {
        // 1. 좌표 문제 없으면 추출
        Optional<Coordinate> opCoordinate = parseXY(raw.X(), raw.Y());
        if (opCoordinate.isEmpty()) {
            return Optional.empty();
        }
        Coordinate coordinate = opCoordinate.get();

        // 2. x,y를 정보를 통해 이미 저장되어있으면 재사용, 없으면 저장
        TennisCourtLocation tennisCourtLocation = courtLocationRepository.findByXAndY(coordinate.x(), coordinate.y()).orElseGet(() -> makeCourtLocation(coordinate));

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
                .tennisCourtLocation(tennisCourtLocation)
                .build();

        TennisCourt savedTennisCourt = tennisCourtRepository.save(court);
        return Optional.of(TennisCourtResponse.from(savedTennisCourt));
    }

    private TennisCourtLocation makeCourtLocation(Coordinate coordinate) {
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

        TennisCourtLocation tennisCourtLocation = TennisCourtLocation.builder()
                .fullAddress(fullAddress)
                .roadAddress(recentAddress)
                .region_one_depth(region1)
                .region_two_depth(region2)
                .region_three_depth(region3)
                .x(x)
                .y(y)
                .build();

        return courtLocationRepository.save(tennisCourtLocation);
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
