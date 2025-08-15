package dgu.umc_app.domain.report.service;

import dgu.umc_app.domain.report.entity.LastMonthReport;
import dgu.umc_app.domain.report.repository.LastMonthReportRepository;
import dgu.umc_app.domain.report.repository.ReportRepository;
import dgu.umc_app.domain.user.entity.User;
import dgu.umc_app.domain.user.repository.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class LastMonthReportSaveService {

    private final ReportRepository reportRepository;
    private final LastMonthReportRepository lastMonthReportRepository;
    private final ReportQueryService reportQueryService; // ReportResponse 조립 재사용
    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final MonthlyResetService monthlyResetService;

    private static final int DAYS = 7, SLOTS = 12, LEN = DAYS * SLOTS; // 84
    private static final int BATCH = 500;

    /** 매달 1일 00:05 KST에 지난달 스냅샷 생성 */
    @Transactional
    @Scheduled(cron = "0 5 0 1 * *", zone = "Asia/Seoul")
    public void snapshotLastMonth() {
        var zone = java.time.ZoneId.of("Asia/Seoul");
        var last = java.time.YearMonth.now(zone).minusMonths(1);
        int y = last.getYear(), m = last.getMonthValue();

        // '스냅샷 기준시각(as-of)': 지난달의 마지막 순간
        var asOf = last.atEndOfMonth().atTime(23, 59, 59);

        var userIds = reportRepository.findOpenedUserIds(y, m);
        for (Long userId : userIds) {
            // 이미 저장되어 있으면 건너뛰기 (write-once)
            if (lastMonthReportRepository.existsByUserIdAndYearAndMonth(userId, y, m)) continue;
            try {
                // 지난달 ReportResponse 생성
                var rr = reportQueryService.getMonthlyReport(userId, y, m);
                var json = objectMapper.writeValueAsString(rr);
                var userRef = userRepository.getReferenceById(userId);
                lastMonthReportRepository.save(LastMonthReport.of(userRef, y, m, json, asOf));

            } catch (org.springframework.dao.DataIntegrityViolationException dup) {
                // 동시 실행 등으로 이미 들어갔으면 무시
            } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
                log.error("LastMonthReport 직렬화 실패: userId={}, ym={}-{}", userId, y, m, e);
            }
        }
        log.info("LastMonthReport rollover completed for {}-{}", y, m);

        List<Long> allIds = userRepository.findAllIds();
        for (int i = 0; i < allIds.size(); i += BATCH) {
            var slice = allIds.subList(i, Math.min(i + BATCH, allIds.size()));
            monthlyResetService.resetBatch(slice);
        }
    }

}
