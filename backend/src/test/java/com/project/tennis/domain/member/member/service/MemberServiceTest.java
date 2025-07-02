package com.project.tennis.domain.member.member.service;

import com.project.tennis.domain.member.member.dto.request.MemberCreateRequest;
import com.project.tennis.domain.member.member.dto.response.MemberCreateResponse;
import com.project.tennis.domain.member.member.entity.Member;
import com.project.tennis.domain.member.member.entity.enums.Role;
import com.project.tennis.domain.member.member.entity.enums.SocialProvider;
import com.project.tennis.domain.member.member.repository.MemberRepository;
import com.project.tennis.global.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;


    @Test
    @DisplayName("회원가입 성공")
    void create_success() {
        // 1. given
        MemberCreateRequest request = new MemberCreateRequest("user", "email@test.com", "nickname", "pw1234");

        // 값 지정
        given(memberRepository.existsByEmail(request.email())).willReturn(false);
        given(memberRepository.existsByUsername(request.username())).willReturn(false);
        given(memberRepository.existsByNickname(request.nickname())).willReturn(false);
        given(passwordEncoder.encode(request.password())).willReturn("encode-pw");

        // 저장될 Member 모의 반환
        Member savedMember = Member.builder()
                .id(UUID.randomUUID().toString())
                .email(request.email())
                .username(request.username())
                .nickname(request.nickname())
                .role(Role.USER)
                .provider(SocialProvider.NONE)
                .password("encode-pw")
                .build();

        given(memberRepository.save(any(Member.class))).willReturn(savedMember);

        // when
        MemberCreateResponse response = memberService.createMember(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.id()).isNotNull();
        assertThat(response.email()).isEqualTo(request.email());
        assertThat(response.username()).isEqualTo(request.username());
        assertThat(response.nickname()).isEqualTo(request.nickname());
        assertThat(response.role()).isEqualTo(Role.USER.name());
    }

    @Test
    @DisplayName("회원가입 실패 - 이메일 중복")
    void create_fail_duplicate_email() {
        // given
        MemberCreateRequest request = new MemberCreateRequest("user", "email@test.com", "nickname", "pw1234");

        given(memberRepository.existsByEmail(request.email())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> memberService.createMember(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("이미 사용 중인 이메일입니다."); // 예외 메시지는 실제 사용한 메시지에 따라 수정
    }

    @DisplayName("회원가입 실패 - 아이디 중복")
    @Test
    void create_fail_duplicate_username() {
        // given
        MemberCreateRequest request = new MemberCreateRequest("user", "email@test.com", "nickname", "pw1234");

        given(memberRepository.existsByUsername(request.username())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> memberService.createMember(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("이미 사용 중인 아이디입니다.");
    }

    @DisplayName("회원가입 실패 - 닉네임 중복")
    @Test
    void create_fail_duplicate_nickname() {
        // given
        MemberCreateRequest request = new MemberCreateRequest("user", "email@test.com", "nickname", "pw1234");

        given(memberRepository.existsByEmail(request.email())).willReturn(false);
        given(memberRepository.existsByUsername(request.username())).willReturn(false);
        given(memberRepository.existsByNickname(request.nickname())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> memberService.createMember(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("이미 사용 중인 닉네임입니다.");
    }
}