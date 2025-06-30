package com.project.tennis.domain.member.member.service;

import com.project.tennis.domain.member.member.dto.request.MemberCreateRequest;
import com.project.tennis.domain.member.member.dto.response.MemberCreateResponse;
import com.project.tennis.domain.member.member.entity.Member;
import com.project.tennis.domain.member.member.repository.MemberRepository;
import com.project.tennis.global.exception.BusinessException;
import com.project.tennis.global.response.RsCode;
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
}
