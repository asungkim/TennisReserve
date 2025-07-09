package com.project.tennis.global.exception.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.tennis.global.exception.BusinessException;
import com.project.tennis.global.response.RsCode;
import com.project.tennis.global.response.RsConstant;
import com.project.tennis.global.response.RsData;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;

/***
 * 스프링 시큐리티 필터에서 인증 실패 시 예외 처리
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws
            IOException {
        writeUnauthorizedResponse(response, e);
    }

    public void commence(HttpServletRequest request, HttpServletResponse response, BusinessException e) throws
            IOException {
        writeUnauthorizedResponse(response, e);
    }

    private void writeUnauthorizedResponse(HttpServletResponse response, Exception e) throws IOException {
        log.error("[JwtAuthenticationEntryPoint] ex", e);

        response.setStatus(RsConstant.UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        String json = objectMapper.writeValueAsString(RsData.from(RsCode.UNAUTHENTICATED));

        try (PrintWriter writer = response.getWriter()) {
            writer.write(json);
            writer.flush();
        }
    }
}
