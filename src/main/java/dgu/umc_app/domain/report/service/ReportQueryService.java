package dgu.umc_app.domain.report.service;

import dgu.umc_app.domain.plan.entity.AiPlan;
import dgu.umc_app.domain.plan.entity.Plan;
import dgu.umc_app.domain.plan.repository.AiPlanRepository;
import dgu.umc_app.domain.plan.repository.PlanRepository;
import dgu.umc_app.domain.report.dto.response.EmotionAchievement;
import dgu.umc_app.domain.report.dto.response.ReportResponse;
//import dgu.umc_app.domain.report.dto.response.SimplePatternSummary;
import dgu.umc_app.domain.report.dto.response.StoragePageResponse;
import dgu.umc_app.domain.report.dto.response.Summary;
import dgu.umc_app.domain.report.entity.LastMonthReport;
import dgu.umc_app.domain.report.exception.ReportErrorCode;
import dgu.umc_app.domain.report.repository.LastMonthReportRepository;
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
import java.time.ZoneId;
import java.util.*;
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
    private final SimpleKeywordService simpleKeywordService;

    private final LastMonthReportRepository lastMonthReportRepository;
    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    private static final int CHUNK_MIN = 8_000;
    private static final int CHUNK_MAX = 12_000;
    private static final int CHUNK_TOPK = 6;    //각 청크에서 상위 몇개 키워드 뽑을지
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
        return reportRepository.existsByUser_IdAndYearAndMonthAndIsOpenedTrue(userId, year, month);
    }

    /** 저번달 리포트 조회**/
    public ReportResponse getLastMonthReport(Long userId) {
        var last = YearMonth.now(ZoneId.of("Asia/Seoul")).minusMonths(1);

        boolean openedLast = reportRepository
                .existsByUser_IdAndYearAndMonthAndIsOpenedTrue(userId, last.getYear(), last.getMonthValue());
        if (!openedLast) throw BaseException.type(ReportErrorCode.LAST_MONTH_REPORT_NOT_OPEN);

        var snap = lastMonthReportRepository
                .findByUserIdAndYearAndMonth(userId, last.getYear(), last.getMonthValue())
                .orElseThrow(() -> BaseException.type(ReportErrorCode.LAST_MONTH_REPORT_NOT_FOUND));

        try {
            String json = snap.getReportJson(); // ← 여기!
            return objectMapper.readValue(json, ReportResponse.class);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new IllegalStateException("스냅샷 파싱 실패", e);
        }
    }


    /**
     * 리포트
     */
    public ReportResponse getMonthlyReport(Long userId, int year, int month) {
        Summary summary = buildProgressSummary(userId, year, month);
        EmotionAchievement emotionAchievement = buildEmotionAchievement(userId, year, month);
        List<String> simpleKeywords = buildSimpleKeywords(userId, year, month, 5);
        return ReportResponse.builder()
                .summary((summary))
                .emotionAchievement(emotionAchievement)
                .simpleKeywords(simpleKeywords)
                .build();
    }

    /**
     * 전체 진행 요약
     * @param userId
     * @param year
     * @param month
     * @return
     */
    public Summary buildProgressSummary(Long userId, int year, int month) {
        List<Plan> independentPlans = planRepository.findIndependentPlans(userId, year, month);
        List<AiPlan> aiPlans = aiPlanRepository.findByUserIdAndMonth(userId, year, month);

        int total = independentPlans.size() + aiPlans.size();

        long independentDone = independentPlans.stream()
                .filter(Plan::isDone)
                .count();

        long aiDone = aiPlans.stream()
                .filter(AiPlan::isDone)
                .count();

        int completed = (int) (independentDone + aiDone);
        return Summary.ofCounts(total, completed);
    }

    /**
     * 감정태그 & 성취도 통계
     * @param userId
     * @param year
     * @param month
     * @return
     */
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

    /** 키워드 추출 (SimpleKeywordService 사용) */
    public List<String> buildSimpleKeywords(Long userId, int year, int month, int topN) {
        YearMonth ym = YearMonth.of(year, month);
        LocalDateTime start = ym.atDay(1).atStartOfDay();
        LocalDateTime end   = ym.atEndOfMonth().atTime(23, 59, 59);

        List<Review> reviews = reviewRepository.findByPlanUserIdAndCreatedAtBetween(userId, start, end);
        if (reviews.isEmpty()) return List.of();

        // 1) 월간 텍스트 합치기
        String monthText = reviews.stream()
                .map(r -> Optional.ofNullable(r.getMemo()).orElse(""))
                .collect(Collectors.joining("\n"));

        // 2) 길이에 따라: 짧으면 1회, 길면 청크
        if (monthText.length() <= CHUNK_MAX) {
            return simpleKeywordService.extractTopTerms(List.of(monthText), topN);
        }

        // 3) 청크 분할(문장/줄바꿈 경계 우선)
        List<String> chunks = chunkByCharBudget(monthText, CHUNK_MIN, CHUNK_MAX);

        // 4) 청크별 TopK 받아와서 로컬 병합
        Map<String, Integer> freq = new HashMap<>();
        for (String chunk : chunks) {
            List<String> part = simpleKeywordService.extractTopTerms(List.of(chunk), CHUNK_TOPK);
            for (String item : part) {
                String norm = normalize(item);
                if (!norm.isBlank()) freq.merge(norm, 1, Integer::sum);
            }
        }

        // 5) 최종 TopN 반환(빈도 → 사전순 tie-break)
        return freq.entrySet().stream()
                .sorted(Map.Entry.<String,Integer>comparingByValue().reversed()
                        .thenComparing(Map.Entry::getKey))
                .limit(topN)
                .map(Map.Entry::getKey)
                .toList();
    }

    private List<String> chunkByCharBudget(String s, int min, int max) {
        List<String> out = new ArrayList<>();
        int start = 0;
        while (start < s.length()) {
            int end = Math.min(start + max, s.length());
            if (end < s.length()) {
                int lastDot = s.lastIndexOf('.', end);
                int lastNL  = s.lastIndexOf('\n', end);
                int cut = Math.max(Math.max(lastDot, lastNL), start + min);
                end = Math.max(cut, start + min);
            }
            out.add(s.substring(start, end));
            start = end;
        }
        return out;
    }

    private String normalize(String s) {
        if (s == null) return "";
        return s.trim().replaceAll("\\s+", " ").toLowerCase();
    }
}
