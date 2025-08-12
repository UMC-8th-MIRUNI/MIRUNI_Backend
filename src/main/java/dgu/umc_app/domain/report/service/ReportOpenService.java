package dgu.umc_app.domain.report.service;

import dgu.umc_app.domain.report.entity.Report;
import dgu.umc_app.domain.report.exception.ReportErrorCode;
import dgu.umc_app.domain.report.repository.ReportRepository;
import dgu.umc_app.domain.user.entity.User;
import dgu.umc_app.domain.user.exception.UserErrorCode;
import dgu.umc_app.domain.user.repository.UserRepository;
import dgu.umc_app.global.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReportOpenService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final ReportQueryService reportQueryService;

    @Transactional
    public void openThisMonth(Long userId, int year, int month) {

        //리포트 행 없으면 생성 시도(경합 허용)
        reportRepository.findByUserIdAndYearAndMonth(userId, year, month)
                .orElseGet(() -> {
                    try {
                        User userRef = userRepository.getReferenceById(userId);
                        Report report = Report.builder()
                                .user(userRef)
                                .year(year)
                                .month(month)
                                .build();
                        return reportRepository.save(report);
                    } catch (org.springframework.dao.DataIntegrityViolationException e) {
                        // 동시 경합으로 이미 생성됨 → 이후 단계로 진행
                        return reportRepository.findByUserIdAndYearAndMonth(userId, year, month)
                                .orElseThrow();
                    }
                });

        //서버 재검증(완료율)
        int completion = reportQueryService.buildProgressSummary(userId, year, month)
                .completionRatePercent();
        if (completion < 80) {
            throw BaseException.type(ReportErrorCode.OPEN_CONDITION_NOT_MET);
        }

        //아직 열지 않았을 때만 '열림'으로 전환
        int opened = reportRepository.markOpenedIfNotOpened(
                userId, year, month, java.time.LocalDateTime.now(), 30
        );
        if (opened == 0) {
            // 이미 열려 있으면 추가 차감 없이 종료
            return;
        }

        //땅콩 원자 차감(잔액 부족/경합 시 0) → 전체 롤백
        int spent = userRepository.trySpendPeanuts(userId, 30);
        if (spent == 0) {
            // 차감 실패 → 트랜잭션 롤백되며 '열림'도 취소됨
            throw BaseException.type(ReportErrorCode.INSUFFICIENT_PEANUT);
        }
    }
}
