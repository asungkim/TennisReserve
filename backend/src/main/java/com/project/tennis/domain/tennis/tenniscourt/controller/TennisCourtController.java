package com.project.tennis.domain.tennis.tenniscourt.controller;

import com.project.tennis.domain.tennis.tenniscourt.dto.request.TennisCourtCreateRequest;
import com.project.tennis.domain.tennis.tenniscourt.dto.response.TennisCourtResponse;
import com.project.tennis.domain.tennis.tenniscourt.service.TennisCourtService;
import com.project.tennis.global.response.RsCode;
import com.project.tennis.global.response.RsData;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tennisCourt")
public class TennisCourtController {

    private final TennisCourtService tennisCourtService;

    @PostMapping
    public RsData<TennisCourtResponse> createTennisCourt(@RequestBody @Valid TennisCourtCreateRequest request) {
        return RsData.from(RsCode.SUCCESS, tennisCourtService.createTennisCourt(request));
    }
}
