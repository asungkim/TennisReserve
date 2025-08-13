package com.project.tennis.domain.tennis.tenniscourt.dto.request;

import jakarta.validation.constraints.NotBlank;

public record TennisCourtRequest(
        @NotBlank(message = "테니스장 이름은 필수 입력값입니다.")
        String name,

        @NotBlank(message = "이미지 URL은 필수 입력값입니다.")
        String imageUrl,

        @NotBlank(message = "전화번호는 필수 입력값입니다.")
        String phoneNumber
) {
}
