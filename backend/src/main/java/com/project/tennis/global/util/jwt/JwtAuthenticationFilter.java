package com.project.tennis.global.util.jwt;

import com.project.tennis.global.exception.BusinessException;
import com.project.tennis.global.exception.jwt.JwtAuthenticationEntryPoint;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.project.tennis.global.response.RsCode.UNAUTHENTICATED;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final static String HEADER_AUTHORIZATION = "Authorization";
    private final JwtProvider jwtProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;


    /**
     * 스프링 시큐리티 필터에서 인증 처리
     **/
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String uri = request.getRequestURI();
        String method = request.getMethod();

        // 0. 인증하지 않고 통과시킬 경로 설정
        if (JwtWhiteListPaths.isWhiteListed(uri, method)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 1. 헤더로부터 토큰을 가져옴
        String authorizationHeader = request.getHeader(HEADER_AUTHORIZATION);
        String accessToken = jwtProvider.getTokenFromHeader(authorizationHeader);

        // 2. jwt 유효한지 검증
        if (!jwtProvider.isValidToken(accessToken)) {
            jwtAuthenticationEntryPoint.commence(request, response, new BusinessException(UNAUTHENTICATED));
            return;
        }

        // 3. jwt를 통해 사용자 인증 정보 준비
        Authentication authentication = jwtProvider.getAuthentication(accessToken);

        // 4. SecurityContext에 사용자 인증 정보 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
}
