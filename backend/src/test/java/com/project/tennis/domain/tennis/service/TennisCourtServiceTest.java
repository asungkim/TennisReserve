package com.project.tennis.domain.tennis.service;

import com.project.tennis.domain.tennis.tenniscourt.dto.request.TennisCourtRequest;
import com.project.tennis.domain.tennis.tenniscourt.dto.response.TennisCourtResponse;
import com.project.tennis.domain.tennis.tenniscourt.entity.TennisCourt;
import com.project.tennis.domain.tennis.tenniscourt.repository.CourtLocationRepository;
import com.project.tennis.domain.tennis.tenniscourt.repository.TennisCourtRepository;
import com.project.tennis.domain.tennis.tenniscourt.service.TennisCourtService;
import com.project.tennis.external.kakao.service.KakaoLocationService;
import com.project.tennis.external.tennis.service.TennisApiService;
import com.project.tennis.global.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TennisCourtServiceTest {

    @InjectMocks
    private TennisCourtService tennisCourtService;

    @Mock
    private TennisApiService tennisApiService;

    @Mock
    private KakaoLocationService kakaoLocationService;

    @Mock
    private TennisCourtRepository tennisCourtRepository;

    @Mock
    private CourtLocationRepository courtLocationRepository;

    @Nested
    @DisplayName("테니스장 생성 테스트")
    class CreateTennisCourt {

        @Test
        @DisplayName("테니스장 생성 성공")
        void create_tennis_court_success() {
            // given
            TennisCourtRequest request = new TennisCourtRequest("테니스장", "image.url", "010-1234-5678");
            given(tennisCourtRepository.existsByName(request.name())).willReturn(false);

            TennisCourt savedCourt = TennisCourt.builder()
                    .name(request.name())
                    .imageUrl(request.imageUrl())
                    .phoneNumber(request.phoneNumber())
                    .build();
            given(tennisCourtRepository.save(any(TennisCourt.class))).willReturn(savedCourt);

            // when
            TennisCourtResponse response = tennisCourtService.createTennisCourt(request);

            // then
            assertThat(response).isNotNull();
            assertThat(response.name()).isEqualTo(request.name());
        }

        @Test
        @DisplayName("테니스장 생성 실패 - 이름 중복")
        void create_tennis_court_fail_duplicate_name() {
            // given
            TennisCourtRequest request = new TennisCourtRequest("테니스장", "image.url", "010-1234-5678");
            given(tennisCourtRepository.existsByName(request.name())).willReturn(true);

            // when & then
            assertThatThrownBy(() -> tennisCourtService.createTennisCourt(request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("이미 사용 중인 테니스장 이름입니다.");
        }
    }

    @Nested
    @DisplayName("테니스장 단일 조회")
    class GetTennisCourt {
        @Test
        @DisplayName("모든 테니스장 조회")
        void get_all_tennis_courts() {
            // given
            List<TennisCourt> courts = List.of(
                    TennisCourt.builder().id(1L).name("테니스장1").build(),
                    TennisCourt.builder().id(2L).name("테니스장2").build()
            );
            given(tennisCourtRepository.findAll()).willReturn(courts);

            // when
            List<TennisCourtResponse> responses = tennisCourtService.getTennisCourts();

            // then
            assertThat(responses).hasSize(2);
            assertThat(responses.get(0).name()).isEqualTo("테니스장1");
        }

        @Test
        @DisplayName("단일 테니스장 조회 성공")
        void get_tennis_court_success() {
            // given
            Long courtId = 1L;
            TennisCourt court = TennisCourt.builder().id(courtId).name("테니스장").build();
            given(tennisCourtRepository.findById(courtId)).willReturn(Optional.of(court));

            // when
            TennisCourtResponse response = tennisCourtService.getTennisCourt(courtId);

            // then
            assertThat(response).isNotNull();
            assertThat(response.id()).isEqualTo(courtId);
        }

        @Test
        @DisplayName("단일 테니스장 조회 실패 - 존재하지 않는 ID")
        void get_tennis_court_fail_not_found() {
            // given
            Long courtId = 1L;
            given(tennisCourtRepository.findById(courtId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> tennisCourtService.getTennisCourt(courtId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("요청하신 리소스를 찾을 수 없습니다.");
        }
    }


    @Nested
    @DisplayName("테니스장 수정")
    class ModifyTennisCourt {

        @Test
        @DisplayName("수정 성공")
        void modify_tennis_court_success() {
            // given
            Long courtId = 1L;
            TennisCourtRequest request = new TennisCourtRequest("새로운 테니스장", "new.image.url", "010-8765-4321");
            TennisCourt existingCourt = TennisCourt.builder().id(courtId).name("오래된 테니스장").build();
            given(tennisCourtRepository.findById(courtId)).willReturn(Optional.of(existingCourt));

            // when
            TennisCourtResponse response = tennisCourtService.modifyTennisCourt(request, courtId);

            // then
            assertThat(response.name()).isEqualTo("새로운 테니스장");
            assertThat(response.imageUrl()).isEqualTo("new.image.url");
            assertThat(response.phoneNumber()).isEqualTo("010-8765-4321");
        }

        @Test
        @DisplayName("수정 실패 - 존재하지 않는 ID")
        void modify_tennis_court_fail_not_found() {
            // given
            Long courtId = 999L;
            TennisCourtRequest request = new TennisCourtRequest("테니스장", "image.url", "010-0000-0000");
            given(tennisCourtRepository.findById(courtId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> tennisCourtService.modifyTennisCourt(request, courtId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("요청하신 리소스를 찾을 수 없습니다.");
        }
    }

    @Nested
    @DisplayName("테니스장 삭제")
    class DeleteTennisCourt {

        @Test
        @DisplayName("삭제 성공")
        void delete_tennis_court_success() {
            // given
            Long courtId = 1L;
            TennisCourt court = TennisCourt.builder().id(courtId).name("삭제될 테니스장").build();
            given(tennisCourtRepository.findById(courtId)).willReturn(Optional.of(court));

            // when
            tennisCourtService.deleteTennisCourt(courtId);

            // then
            verify(tennisCourtRepository, times(1)).delete(court);
        }

        @Test
        @DisplayName("삭제 실패 - 존재하지 않는 ID")
        void delete_tennis_court_fail_not_found() {
            // given
            Long courtId = 999L;
            given(tennisCourtRepository.findById(courtId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> tennisCourtService.deleteTennisCourt(courtId))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("요청하신 리소스를 찾을 수 없습니다.");
        }
    }
}
