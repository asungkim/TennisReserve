package com.project.tennis.domain.member.member.dto.request;

import com.project.tennis.domain.member.member.entity.Member;
import com.project.tennis.domain.member.member.entity.enums.Role;
import com.project.tennis.domain.member.member.entity.enums.SocialProvider;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

public record MemberCreateRequest(
        @NotBlank(message = "아이디는 필수 입력입니다.")
        @Pattern(
                regexp = "^[a-z0-9]{4,20}$",
                message = "아이디는 4~20자 소문자+숫자만 허용됩니다."
        )
        String username,

        @NotBlank(message = "이메일은 필수 입력입니다.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        String email,

        @NotBlank(message = "비밀번호는 필수 입력입니다.")
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=])[A-Za-z\\d!@#$%^&*()_+\\-=]{8,20}$",
                message = "비밀번호는 8~20자 영문, 숫자, 특수문자를 포함해야 합니다."
        )
        String password,

        @NotBlank(message = "닉네임은 필수 입력입니다.")
        @Pattern(
                regexp = "^[가-힣a-zA-Z0-9]{2,20}$",
                message = "닉네임은 2~20자 한글, 영문, 숫자만 허용됩니다."
        )
        String nickname
) {
    public Member toEntity(PasswordEncoder passwordEncoder, String profileImage) {
        return Member.builder()
                .id("member-" + UUID.randomUUID())
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(password))
                .nickname(nickname)
                .role(Role.USER)
                .profileImage(profileImage)
                .provider(SocialProvider.NONE)
                .build();
    }
}
