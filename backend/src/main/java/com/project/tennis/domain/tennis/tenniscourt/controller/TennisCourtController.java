package com.project.tennis.domain.tennis.tenniscourt.controller;

import com.project.tennis.domain.tennis.tenniscourt.dto.request.TennisCourtRequest;
import com.project.tennis.domain.tennis.tenniscourt.dto.response.TennisCourtResponse;
import com.project.tennis.domain.tennis.tenniscourt.service.TennisCourtService;
import com.project.tennis.global.response.RsCode;
import com.project.tennis.global.response.RsData;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tennis-courts")
public class TennisCourtController {

    private final TennisCourtService tennisCourtService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public RsData<TennisCourtResponse> createTennisCourt(@RequestBody @Valid TennisCourtRequest request) {
        return RsData.from(RsCode.SUCCESS, tennisCourtService.createTennisCourt(request));
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public RsData<List<TennisCourtResponse>> getTennisCourts() {
        return RsData.from(RsCode.SUCCESS, tennisCourtService.getTennisCourts());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public RsData<TennisCourtResponse> getTennisCourt(@PathVariable Long id) {
        return RsData.from(RsCode.SUCCESS, tennisCourtService.getTennisCourt(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public RsData<TennisCourtResponse> modifyTennisCourt(
            @RequestBody @Valid TennisCourtRequest request,
            @PathVariable Long id
    ) {
        return RsData.from(RsCode.SUCCESS, tennisCourtService.modifyTennisCourt(request, id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public RsData<?> deleteTennisCourt(@PathVariable Long id) {
        tennisCourtService.deleteTennisCourt(id);
        return RsData.from(RsCode.SUCCESS);
    }
}
