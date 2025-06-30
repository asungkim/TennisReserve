package com.project.tennis.global.aspect;


import com.project.tennis.global.response.RsData;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class ResponseAspect {
    private final HttpServletResponse response;

    @Around("execution(* com.project.tennis..*RestController.*(..))")
    public Object responseAspect(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();

        // 응답코드를 설정해준다
        if (result instanceof RsData<?> rsData) {
            int statusCode = rsData.code();
            response.setStatus(statusCode);
        }

        return result;
    }
}
