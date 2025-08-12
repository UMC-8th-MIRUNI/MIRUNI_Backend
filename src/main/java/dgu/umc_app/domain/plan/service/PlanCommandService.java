package dgu.umc_app.domain.plan.service;

import dgu.umc_app.domain.plan.dto.request.PlanCreateRequest;
import dgu.umc_app.domain.plan.dto.request.PlanDelayRequest;
import dgu.umc_app.domain.plan.dto.request.PlanSplitRequest;
import dgu.umc_app.domain.plan.dto.response.PlanCreateResponse;
import dgu.umc_app.domain.plan.dto.response.PlanDelayResponse;
import dgu.umc_app.domain.plan.dto.response.PlanSplitResponse;
import dgu.umc_app.domain.plan.entity.*;
import dgu.umc_app.domain.plan.exception.AiPlanErrorCode;
import dgu.umc_app.domain.plan.exception.PlanErrorCode;
import dgu.umc_app.domain.plan.repository.AiPlanRepository;
import dgu.umc_app.domain.plan.repository.PlanRepository;
import dgu.umc_app.domain.user.entity.User;
import dgu.umc_app.global.exception.BaseException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanCommandService {

    private final PlanRepository planRepository;
    private final AiPlanRepository aiPlanRepository;
    private final AiSplitService aiSplitService;

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");
    private static final int SLOTS_PER_DAY = 12;
    private static final int DAYS = 7;
    private static final int TOTAL_BUCKETS = DAYS * SLOTS_PER_DAY;
    private static final long FOCUS_MINUTES_THRESHOLD = 30;

    private LocalDateTime toKst(LocalDateTime t) {
        return t.atZone(ZoneId.systemDefault()).withZoneSameInstant(KST).toLocalDateTime();
    }
    private LocalDateTime bucketStartKst(LocalDateTime kst) {
        int evenHour = (kst.getHour() / 2) * 2;
        return kst.withHour(evenHour).withMinute(0).withSecond(0).withNano(0);
    }
    private int dayIndexKst(LocalDateTime kst) { return kst.getDayOfWeek().getValue() - 1; }
    private int slotIndexKst(LocalDateTime kst) { return kst.getHour() / 2; }
    private int flatIndexKst(LocalDateTime kst) { return dayIndexKst(kst) * SLOTS_PER_DAY + slotIndexKst(kst); }

    private void incrementDelayBucket(User user, LocalDateTime stoppedAt) {
        int idx = flatIndexKst(toKst(stoppedAt));
        List<Long> slots = user.getDelayList();
        slots.set(idx, slots.get(idx) + 1L);
    }

    private void incrementFocusBucketsTouching(User user,
                                               LocalDateTime scheduledStart,
                                               LocalDateTime stoppedAt,
                                               long execMinutes) {
        if (execMinutes < FOCUS_MINUTES_THRESHOLD || scheduledStart == null || stoppedAt == null) return;

        LocalDateTime startKst = toKst(scheduledStart);
        LocalDateTime endKst   = toKst(stoppedAt);
        if (!endKst.isAfter(startKst)) return;

        List<Long> bins = user.getFocusList();

        LocalDateTime slotStart = bucketStartKst(startKst);
        while (slotStart.isBefore(endKst)) {
            LocalDateTime slotEnd = slotStart.plusHours(2);

            LocalDateTime a = startKst.isAfter(slotStart) ? startKst : slotStart;
            LocalDateTime b = endKst.isBefore(slotEnd) ? endKst : slotEnd;
            if (a.isBefore(b)) {
                int idx = flatIndexKst(slotStart);
                bins.set(idx, bins.get(idx) + 1L);
            }
            slotStart = slotEnd;
        }
    }

    @Transactional
    public PlanCreateResponse createPlan(PlanCreateRequest request, User user) {

        LocalDateTime today = LocalDateTime.now();

        if (request.deadline().isBefore(today)
                || request.scheduledStart().isBefore(today)) {
            throw BaseException.type(PlanErrorCode.INVALID_DATE_RANGE);
        }

        Plan savedPlan = planRepository.save(request.toEntity(user));
        return PlanCreateResponse.from(savedPlan);
    }

    @Transactional
    public List<PlanSplitResponse> splitPlan(Long planId, PlanSplitRequest request, User user) {

        // 1. 임시로 등록된 plan 조회하기
        Plan plan = planRepository.findByIdWithUserId(planId, user.getId())
                .orElseThrow(() -> new BaseException(PlanErrorCode.PLAN_NOT_FOUND));

        // 2. AI 분할 요청
        List<PlanSplitResponse> splitResponses = aiSplitService.requestSplitResponseOnly(
                plan.getTitle(),
                plan.getDeadline(),
                plan.getScheduledStart(),
                plan.getPriority(),
                request.planType(),
                request.taskRange(),
                request.detailRequest(),
                plan
        );

        // 3. 엔티티 변환 및 저장
        List<AiPlan> aiPlans = PlanSplitResponse.toEntities(
                splitResponses,
                plan,
                request.planType(),
                request.taskRange()
        );
        aiPlanRepository.saveAll(aiPlans);

        planRepository.save(plan);

        return splitResponses;
    }


    @Transactional
    public PlanDelayResponse delayPlan(Long planId, PlanDelayRequest request, User user) {
        if (request.category() != Category.AI && request.category() != Category.BASIC) {
            throw BaseException.type(PlanErrorCode.INVALID_CATEGORY);
        }

        Plan plan = planRepository.findByIdWithUserId(planId, user.getId())
                .orElseThrow(() -> new BaseException(PlanErrorCode.PLAN_NOT_FOUND));

        if (request.expectedMinutes() == null) {
            throw BaseException.type(AiPlanErrorCode.EXPECTED_MINUTES_REQUIRED);
        }

        LocalDateTime stoppedAt = LocalDateTime.now(); // 중지 시각
        LocalDateTime newStart = request.newStartDateTime(); // 재시작 시각(요청 값)
        LocalDateTime originalStart = plan.getScheduledStart(); // 예정 시작시각

        long delayDelta;
        if (originalStart != null && stoppedAt.isBefore(originalStart)) {
            // 예정보다 일찍 중지 → 지연시간 미계산
            delayDelta = 0L;
        } else {
            long between = ChronoUnit.MINUTES.between(stoppedAt, newStart);
            delayDelta = Math.max(between, 0L);
        }

        long execDelta;
        if (originalStart != null && stoppedAt.isBefore(originalStart)) {
            // 예정보다 일찍 중지 → 프론트에서 전달한 실행 시간 사용
            execDelta = Math.max(request.executeTime(), 0L);
        } else if (originalStart != null) {
            long ran = ChronoUnit.MINUTES.between(originalStart, stoppedAt);
            execDelta = Math.max(ran, 0L);
        } else {
            execDelta = 0L;
        }

        // 엔티티 업데이트
        plan.updateScheduleStart(newStart);
        plan.updateScheduleEnd(newStart.plusMinutes(request.expectedMinutes()));
        plan.updateStatus(Status.PAUSED);
        plan.updateStoppedAt(stoppedAt);

        user.updateDelayTimes(safePlus(user.getDelayTimes(), delayDelta));
        user.updateExecuteTimes(safePlus(user.getExecuteTimes(), execDelta));

        incrementDelayBucket(user, stoppedAt);
        incrementFocusBucketsTouching(user, originalStart, stoppedAt, execDelta);

        return PlanDelayResponse.from(plan, delayDelta, execDelta, stoppedAt);
    }

    private static long safePlus(Long current, long delta) {
        if (delta <= 0) return current == null ? 0L : current;
        return (current == null ? 0L : current) + delta;
    }

    @Transactional
    public PlanDelayResponse delayAiPlan(Long aiPlanId, PlanDelayRequest request, User user) {
        if (request.category() != Category.AI && request.category() != Category.BASIC) {
            throw BaseException.type(PlanErrorCode.INVALID_CATEGORY);
        }

        AiPlan aiPlan = aiPlanRepository.findById(aiPlanId)
                .filter(ap -> ap.getPlan() != null && ap.getPlan().getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new BaseException(AiPlanErrorCode.AIPLAN_NOT_FOUND));

        if (request.expectedMinutes() == null) {
            throw BaseException.type(AiPlanErrorCode.EXPECTED_MINUTES_REQUIRED);
        }

        LocalDateTime stoppedAt = LocalDateTime.now();
        LocalDateTime newStart = request.newStartDateTime();
        LocalDateTime originalStart = aiPlan.getScheduledStart();

        long delayDelta;
        if (originalStart != null && stoppedAt.isBefore(originalStart)) {
            // 예정보다 일찍 중지 → 지연시간 미계산
            delayDelta = 0L;
        } else {
            long between = ChronoUnit.MINUTES.between(stoppedAt, newStart);
            delayDelta = Math.max(between, 0L);
        }

        long execDelta;
        if (originalStart != null && stoppedAt.isBefore(originalStart)) {
            // 예정보다 일찍 중지 → 프론트에서 전달한 실행 시간 사용
            execDelta = Math.max(request.executeTime(), 0L);
        } else if (originalStart != null) {
            long ran = ChronoUnit.MINUTES.between(originalStart, stoppedAt);
            execDelta = Math.max(ran, 0L);
        } else {
            execDelta = 0L;
        }

        // 예상 소요시간: 있으면 갱신, 아니면 기존 유지
        if (request.expectedMinutes() != null) {
            Long effectiveExpected = request.expectedMinutes().longValue();
            aiPlan.updateExpectedDuration(effectiveExpected);
        }

        // 엔티티 업데이트
        aiPlan.updateScheduleStart(newStart);
        aiPlan.updateScheduleEnd(newStart.plusMinutes(request.expectedMinutes()));
        aiPlan.updateStatus(Status.PAUSED);
        aiPlan.updateStoppedAt(stoppedAt);
        aiPlan.updateIsDelayed(true);

        user.updateDelayTimes(safePlus(user.getDelayTimes(), delayDelta));
        user.updateExecuteTimes(safePlus(user.getExecuteTimes(), execDelta));

        incrementDelayBucket(user, stoppedAt);
        incrementFocusBucketsTouching(user, originalStart, stoppedAt, execDelta);

        return PlanDelayResponse.from(aiPlan, delayDelta, execDelta, stoppedAt);
    }
}
