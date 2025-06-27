package com.project.tennis.global.exception;

import com.project.tennis.global.response.RsData;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public RsData<?> handleBusinessException(
            final BusinessException exception
    ) {
        return RsData.from(exception.getRsCode());
    }
}
