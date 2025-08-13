package com.project.tennis.global.init;

import com.project.tennis.domain.tennis.tenniscourt.service.TennisCourtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TennisCourtInitRunner implements ApplicationRunner {

    private final TennisCourtService tennisCourtService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        try {
            tennisCourtService.saveCourtAndLocationByExternalApi();
            log.info("초기 테니스장 저장 완료");
        }
        catch (Exception e) {
            log.error("초기 테니스장 저장 실패");
        }
    }
}
