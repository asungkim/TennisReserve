package com.project.tennis.domain.auth.controller;

import com.project.tennis.domain.auth.service.AuthTokenService;
import com.project.tennis.domain.member.member.dto.response.AuthTokenResponse;
import com.project.tennis.global.response.RsData;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static com.project.tennis.global.response.RsCode.CREATED;

@RestController
@RequestMapping("/api/tokens")
@RequiredArgsConstructor
public class AuthTokenController {

    private final AuthTokenService authTokenService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RsData<AuthTokenResponse> reissueToken(
            @CookieValue(value = "refreshToken") String refreshToken,
            HttpServletResponse response
    ) {
        return RsData.from(CREATED, authTokenService.reissueAccessToken(refreshToken, response));
    }
}
