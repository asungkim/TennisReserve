package com.project.tennis.domain.member.member.service;

import com.project.tennis.domain.auth.token.service.AuthTokenService;
import com.project.tennis.domain.member.member.dto.request.LoginRequest;
import com.project.tennis.domain.member.member.dto.request.MemberCreateRequest;
import com.project.tennis.domain.member.member.dto.response.AuthTokenResponse;
import com.project.tennis.domain.member.member.dto.response.MemberCreateResponse;
import com.project.tennis.domain.member.member.entity.Member;
import com.project.tennis.domain.member.member.repository.MemberRepository;
import com.project.tennis.global.exception.BusinessException;
import com.project.tennis.global.response.RsCode;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthTokenService authTokenService;

    @Value("${custom.member.profile}")
    private String defaultProfile;

    public MemberCreateResponse createMember(MemberCreateRequest request) {

        // 1. Member 요소 중복 검증
        validateDuplicateMember(request);

        // 2. request를 통해 멤버 생성
        Member member = request.toEntity(passwordEncoder, defaultProfile);

        // 3. 멤버 db에 저장
        Member savedMember = memberRepository.save(member);

        // 4. 응답값 리턴
        return MemberCreateResponse.toEntity(savedMember);
    }

    public AuthTokenResponse loginMember(LoginRequest request, HttpServletResponse response) {
        String identifier = request.identifier(); // 아이디 또는 이메일
        String rawPassword = request.password();

        // 1. 이메일인지 아이디인지 확인 후 유저 조회
        Member member = isEmail(identifier)
                ? memberRepository.findByEmail(identifier)
                .orElseThrow(() -> new BusinessException(RsCode.EMAIL_NOT_EXIST))
                : memberRepository.findByUsername(identifier)
                .orElseThrow(() -> new BusinessException(RsCode.USERNAME_NOT_EXIST));

        // 2. 존재한다면 비밀번호 검증
        if (!passwordEncoder.matches(rawPassword, member.getPassword())) {
            throw new BusinessException(RsCode.PASSWORD_NOT_CORRECT);
        }

        // 3. 문제없으면 리프레시 및 액세스 토큰 발급 (쿠키도 발급)
        authTokenService.createRefreshToken(member, response);
        String accessToken = authTokenService.generateAccessToken(member);

        return new AuthTokenResponse(accessToken);
    }

    public void logoutMember(HttpServletResponse response, String memberId) {
        authTokenService.removeRefreshToken(memberId,response);
    }

    private void validateDuplicateMember(MemberCreateRequest request) {
        if (memberRepository.existsByUsername(request.username())) {
            throw new BusinessException(RsCode.DUPLICATE_USERNAME);
        }

        if (memberRepository.existsByEmail(request.email())) {
            throw new BusinessException(RsCode.DUPLICATE_EMAIL);
        }

        if (memberRepository.existsByNickname(request.nickname())) {
            throw new BusinessException(RsCode.DUPLICATE_NICKNAME);
        }
    }

    private boolean isEmail(String identifier) {
        return identifier.contains("@"); // 또는 정규식 검증
    }


}
