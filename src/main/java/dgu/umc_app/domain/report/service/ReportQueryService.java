package dgu.umc_app.domain.report.service;

import com.google.api.gax.rpc.NotFoundException;
import dgu.umc_app.domain.plan.entity.AiPlan;
import dgu.umc_app.domain.plan.entity.Plan;
import dgu.umc_app.domain.plan.repository.AiPlanRepository;
import dgu.umc_app.domain.plan.repository.PlanRepository;
import dgu.umc_app.domain.report.dto.response.EmotionAchievement;
import dgu.umc_app.domain.report.dto.response.ReportResponse;
import dgu.umc_app.domain.report.dto.response.StoragePageResponse;
import dgu.umc_app.domain.report.repository.ReportRepository;
import dgu.umc_app.domain.review.entity.Mood;
import dgu.umc_app.domain.review.entity.Review;
import dgu.umc_app.domain.review.repository.ReviewRepository;
import dgu.umc_app.domain.user.exception.UserErrorCode;
import dgu.umc_app.domain.user.repository.UserRepository;
import dgu.umc_app.global.exception.BaseException;
import lombok.RequiredArgsConstructor;
import dgu.umc_app.domain.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportQueryService {

    private final UserRepository userRepository;
    private final AiPlanRepository aiPlanRepository;
    private final PlanRepository planRepository;
    private final ReportRepository reportRepository;
    private final ReviewRepository reviewRepository;

    /**
     * 리포트 보관함
     * @param userId
     * @param year
     * @param month
     * @return
     */
    public StoragePageResponse getStoragePage(Long userId, int year, int month) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> BaseException.type(UserErrorCode.USER_NOT_FOUND));
        int peanutCount = user.getPeanutCount();

        // 1. 이번달 일정 완료율 계산
        List<AiPlan> aiPlans = aiPlanRepository.findByUserIdAndMonth(userId, year, month);
        long doneAiCount = aiPlans.stream().filter(AiPlan::isDone).count();

        List<Plan> purePlans = planRepository.findIndependentPlans(userId, year, month);
        long donePlanCount = purePlans.stream().filter(Plan::isDone).count();

        long totalCount = aiPlans.size() + purePlans.size();
        int completionRate = totalCount > 0
                ? (int) Math.round((doneAiCount + donePlanCount) * 100.0 / totalCount)
                : 0;

        // 2. 이번달 리포트 오픈 여부
        boolean isOpenedThisMonth = isReportOpenedForMonth(userId, year, month);

        // 3. 저번달 리포트 오픈 여부
        LocalDate lastMonthDate = LocalDate.of(year, month, 1).minusMonths(1);
        boolean isOpenedLastMonth = isReportOpenedForMonth(userId, lastMonthDate.getYear(), lastMonthDate.getMonthValue());

        // 4. 이번달 리포트 오픈 조건 충족 여부
        boolean canOpen = !isOpenedThisMonth && peanutCount >= 30 && completionRate >= 80;

        // 5. UI 구성용 데이터 파생
        String lockState = isOpenedThisMonth ? "열림" : "잠김";
        boolean isOpenButtonVisible = !isOpenedThisMonth && canOpen;

        return StoragePageResponse.from(
                peanutCount,
                completionRate,
                isOpenedThisMonth,
                canOpen,
                isOpenedLastMonth,
                lockState,
                isOpenButtonVisible
        );
    }

    private boolean isReportOpenedForMonth(Long userId, int year, int month) {
        return reportRepository.existsByUserIdAndYearAndMonthAndIsOpenedTrue(userId, year, month);
    }

    /**
     * 리포트
     */
    public ReportResponse getMonthlyReport(Long userId, int year, int month) {
        EmotionAchievement emotionAchievement = buildEmotionAchievement(userId, year, month);

        return ReportResponse.builder()
                .emotionAchievement(emotionAchievement)
                .build();
    }

    public EmotionAchievement buildEmotionAchievement(Long userId, int year, int month) {
        YearMonth ym = YearMonth.of(year, month);
        LocalDateTime start = ym.atDay(1).atStartOfDay();
        LocalDateTime end   = ym.atEndOfMonth().atTime(23, 59, 59);

        List<Review> reviews = reviewRepository.findByPlanUserIdAndCreatedAtBetween(userId, start, end);
        int total = reviews.size();

        // 0건 처리: 모든 감정 0%, 평균 성취도 0
        if (total == 0) {
            Map<Mood, Integer> zeros = new EnumMap<>(Mood.class);
            for (Mood m : Mood.values()) zeros.put(m, 0);
            return EmotionAchievement.builder()
                    .moodPercents(zeros)
                    .averageAchievementPercent(0)
                    .build();
        }

        // 감정별 개수
        Map<Mood, Long> counts = reviews.stream()
                .collect(Collectors.groupingBy(
                        Review::getMood,
                        () -> new EnumMap<>(Mood.class),
                        Collectors.counting()
                ));

        // 감정별 비율(%), 반올림 → int
        Map<Mood, Integer> percents = new EnumMap<>(Mood.class);
        for (Mood m : Mood.values()) {
            long c = counts.getOrDefault(m, 0L);
            int percent = (int) Math.round(c * 100.0 / total);
            percents.put(m, percent);
        }

        // 평균 성취도(%), 반올림 → int
        int avgAchievement = (int) Math.round(
                reviews.stream().mapToInt(Review::getAchievement).average().orElse(0.0)
        );

        return EmotionAchievement.builder()
                .moodPercents(percents)
                .averageAchievementPercent(avgAchievement)
                .build();
    }

}
