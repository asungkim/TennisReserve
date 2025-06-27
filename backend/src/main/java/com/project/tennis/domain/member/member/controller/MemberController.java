package com.project.tennis.domain.member.member.controller;

import com.project.tennis.domain.member.member.dto.request.MemberCreateRequest;
import com.project.tennis.domain.member.member.dto.response.MemberCreateResponse;
import com.project.tennis.domain.member.member.service.MemberService;
import com.project.tennis.global.response.RsData;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.project.tennis.global.response.RsCode.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {
    private final MemberService memberService;

    @PostMapping
    public RsData<MemberCreateResponse> createMember(@RequestBody @Valid MemberCreateRequest request) {
        return RsData.from(SUCCESS, memberService.createMember(request));
    }
}
