package dgu.umc_app.domain.plan.service;

import dgu.umc_app.domain.plan.dto.request.PlanCreateRequest;
import dgu.umc_app.domain.plan.dto.request.PlanDelayRequest;
import dgu.umc_app.domain.plan.dto.request.PlanSplitRequest;
import dgu.umc_app.domain.plan.dto.request.PlanUpdateRequest;
import dgu.umc_app.domain.plan.dto.response.*;
import dgu.umc_app.domain.plan.dto.request.*;
import dgu.umc_app.domain.plan.entity.*;
import dgu.umc_app.domain.plan.exception.AiPlanErrorCode;
import dgu.umc_app.domain.plan.exception.PlanErrorCode;
import dgu.umc_app.domain.plan.repository.AiPlanRepository;
import dgu.umc_app.domain.plan.repository.PlanRepository;
import dgu.umc_app.domain.user.entity.User;
import dgu.umc_app.domain.user.exception.UserErrorCode;
import dgu.umc_app.domain.user.repository.UserRepository;
import dgu.umc_app.global.exception.BaseException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlanCommandService {

    private final UserRepository userRepository;
    private final PlanRepository planRepository;
    private final AiPlanRepository aiPlanRepository;
    private final AiSplitService aiSplitService;

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");
    private static final int SLOTS_PER_DAY = 12;
    private static final int DAYS = 7;
    private static final int TOTAL_BUCKETS = DAYS * SLOTS_PER_DAY;
    private static final long FOCUS_MINUTES_THRESHOLD = 30;
    private static final int SLOT_COUNT = 84;

    private void ensureSlotsInitialized(User user) {
        // delay
        if (user.getDelayList() == null || user.getDelayList().size() != SLOT_COUNT) {
            List<Long> slots = new ArrayList<>(Collections.nCopies(SLOT_COUNT, 0L));
            // 기존 값이 일부라도 있으면 복사 (사이즈 0인 경우 스킵)
            List<Long> old = user.getDelayList();
            if (old != null) {
                for (int i = 0; i < Math.min(old.size(), SLOT_COUNT); i++) {
                    slots.set(i, old.get(i));
                }
            }
            user.updateDelayList(slots);
        }

        // focus
        if (user.getFocusList() == null || user.getFocusList().size() != SLOT_COUNT) {
            List<Long> slots = new ArrayList<>(Collections.nCopies(SLOT_COUNT, 0L));
            List<Long> old = user.getFocusList();
            if (old != null) {
                for (int i = 0; i < Math.min(old.size(), SLOT_COUNT); i++) {
                    slots.set(i, old.get(i));
                }
            }
            user.updateFocusList(slots);
        }
    }

    private LocalDateTime toKst(LocalDateTime t) {
        return t.atZone(ZoneId.systemDefault()).withZoneSameInstant(KST).toLocalDateTime();
    }
    private LocalDateTime bucketStartKst(LocalDateTime kst) {
        int evenHour = (kst.getHour() / 2) * 2;
        return kst.withHour(evenHour).withMinute(0).withSecond(0).withNano(0);
    }
    private int dayIndexKst(LocalDateTime kst) { return kst.getDayOfWeek().ordinal(); }
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
        log.info("[FOCUS] userId={}, execMinutes={}, scheduledStart={}, stoppedAt={}",
                user.getId(), execMinutes, scheduledStart, stoppedAt);
        if (execMinutes < FOCUS_MINUTES_THRESHOLD || scheduledStart == null || stoppedAt == null) return;

        LocalDateTime startKst = toKst(scheduledStart);
        LocalDateTime endKst   = toKst(stoppedAt);
        log.info("[FOCUS] 변환 후 startKst={}, endKst={}", startKst, endKst);

        if (!endKst.isAfter(startKst)) {
            log.warn("[FOCUS] SKIP - endKst가 startKst보다 같거나 이전임");
            return;
        }

        List<Long> bins = user.getFocusList();
        if (bins == null || bins.size() != 84) {
            log.error("[FOCUS] focusList 초기화 안됨 - size={}", (bins == null ? null : bins.size()));
            return;
        }

        LocalDateTime slotStart = bucketStartKst(startKst);
        log.debug("[FOCUS] 시작 slotStart={}", slotStart);

        int touched = 0;
        while (slotStart.isBefore(endKst)) {
            LocalDateTime slotEnd = slotStart.plusHours(2);

            LocalDateTime a = startKst.isAfter(slotStart) ? startKst : slotStart;
            LocalDateTime b = endKst.isBefore(slotEnd) ? endKst : slotEnd;
            if (a.isBefore(b)) {
                int idx = flatIndexKst(slotStart);
                long before = bins.get(idx);
                bins.set(idx, bins.get(idx) + 1L);
                log.debug("[FOCUS] idx={} | slotStart={} ~ slotEnd={} | before={} -> after={}",
                        idx, slotStart, slotEnd, before, bins.get(idx));
                touched++;
            }
            slotStart = slotEnd;
        }
        log.info("[FOCUS] 총 {}개 슬롯 증가 처리 완료", touched);
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
    public PlanStartResponse startPlan(Long planId, Long userId) {
        Plan plan = planRepository.findByIdWithUserId(planId, userId)
                .orElseThrow(() -> new BaseException(PlanErrorCode.PLAN_NOT_FOUND));

        plan.updateStatus(Status.IN_PROGRESS);
        return PlanStartResponse.fromPlan(plan);
    }

    @Transactional
    public PlanStartResponse startAiPlan(Long aiPlanId, Long userId) {
        AiPlan aiPlan = aiPlanRepository.findByIdAndUserId(aiPlanId, userId)
                .orElseThrow(() -> new BaseException(AiPlanErrorCode.AIPLAN_NOT_FOUND));

        aiPlan.updateStatus(Status.IN_PROGRESS);
        return PlanStartResponse.fromAiPlan(aiPlan);
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
    public PlanDetailResponse updatePlan(Long planId, PlanUpdateRequest request, User user) {

        // 1) 소유자 검증 + 로드
        Plan plan = planRepository.findByIdWithUserId(planId, user.getId())
                .orElseThrow(() -> new BaseException(PlanErrorCode.PLAN_NOT_FOUND));

        // 2) 상위 Plan 부분 수정
        request.applyToPlan(plan);

        // 3) 신규/부분수정 검증 (신규 AiPlan not-null, 시간 범위 등)
        validateForMerge(request);

        // 4) 기존 AiPlan 맵(id -> entity)
        Map<Long, AiPlan> existing = plan.getAiPlans().stream()
                .collect(Collectors.toMap(AiPlan::getId, a -> a));

        // 5) AiPlan 컬렉션 부분 수정/추가/삭제 (planType 보존, 신규만 기본값)
        request.mergeIntoAiPlan(plan, existing);

        // 6) stepOrder normalize (1..N)
        long order = 1L;
        for (AiPlan ap : plan.getAiPlans()) {
            ap.updateStepOrder(order++);
        }

        // 7) 저장 및 응답 (일반 일정/AI 일정에 따라 분기)
        if (plan.getAiPlans() == null || plan.getAiPlans().isEmpty()) {
            return PlanDetailResponse.fromPlan(plan);
        } else {
            return PlanDetailResponse.fromAiPlan(plan, plan.getAiPlans());
        }
    }
    private void validateForMerge(PlanUpdateRequest req) {
        if (req.plans() == null) return;

        for (var d : req.plans()) {
            // 신규 + 삭제 조합 방지
            if (d.id() == null && Boolean.TRUE.equals(d.aiDelete())) {
                throw new BaseException(AiPlanErrorCode.INVALID_REQUEST_STATE);
            }

            // 신규 생성 필수값 (AiPlan: description, expectedDuration, scheduledStart, scheduledEnd, planType(신규시 기본값으로 세팅), taskRange)
            if (d.id() == null) {
                boolean missing = d.description() == null
                        || d.expectedDuration() == null
                        || d.date() == null
                        || d.scheduledStartTime() == null
                        || d.scheduledEndTime() == null
                        || req.taskRange() == null;
                if (missing) throw new BaseException(AiPlanErrorCode.INVALID_AIPLAN_FIELDS);
            }

            // 시간 유효성
            if (d.date() != null && d.scheduledStartTime() != null && d.scheduledEndTime() != null) {
                var start = LocalDateTime.of(d.date(), d.scheduledStartTime());
                var end   = LocalDateTime.of(d.date(), d.scheduledEndTime());
                if (end.isBefore(start)) throw new BaseException(AiPlanErrorCode.INVALID_TIME_RANGE);
            }
        }
    }

    @Transactional
    public PlanDelayResponse delayPlan(Long planId, PlanDelayRequest request, User sessionUser) {
        if (request.category() != Category.AI && request.category() != Category.BASIC) {
            throw BaseException.type(PlanErrorCode.INVALID_CATEGORY);
        }
        if (request.actualStart() == null) {
            throw BaseException.type(PlanErrorCode.ACTUAL_START_REQUIRED);
        }
        if (request.executeTime() < 0) {
            throw BaseException.type(PlanErrorCode.INVALID_EXECUTE_MINUTES);
        }

        User user = userRepository.findWithSlotsById(sessionUser.getId())
                .orElseThrow(() -> new BaseException(PlanErrorCode.PLAN_NOT_FOUND));
        ensureSlotsInitialized(user);

        Plan plan = planRepository.findByIdWithUserId(planId, user.getId())
                .orElseThrow(() -> new BaseException(PlanErrorCode.PLAN_NOT_FOUND));

        LocalDateTime originalStart = plan.getScheduledStart(); // 예정 시작시각
        LocalDateTime originalEnd = plan.getScheduledEnd(); // 예정 시작시각
        LocalDateTime stoppedAt = request.actualStart().plusMinutes(request.executeTime()); // 중지한 시각
        LocalDateTime newStart = request.newStartDateTime(); // 재시작 시각(요청 값)

        long expected = calcExpectedMinutes(originalStart, originalEnd);    // 예상 소요 시간
        long remaining = Math.max(0, expected - request.executeTime()); // 남은 예상 소요 시간

        int peanuts = calcPeanuts(request.executeTime(), expected);

        int delayDelta;
        if (originalStart != null && stoppedAt.isBefore(originalStart)) {
            delayDelta = 0;
        } else {
            long between = ChronoUnit.MINUTES.between(stoppedAt, newStart);
            delayDelta = toNonNegativeInt(between);
        }

        int execDelta;
        if (originalStart == null) {
            execDelta = 0;
        } else if (stoppedAt.isBefore(originalStart)) {
            execDelta = Math.max(request.executeTime(), 0); // 프론트 전달값 사용
        } else {
            long ran = ChronoUnit.MINUTES.between(originalStart, stoppedAt);
            execDelta = toNonNegativeInt(ran);
        }

        // 엔티티 업데이트
        plan.updateScheduleStart(newStart);
        plan.updateScheduleEnd(newStart.plusMinutes(remaining));
        plan.updateStatus(Status.PAUSED);
        plan.updateStoppedAt(stoppedAt);

        user.updateDelayTime(safePlusInt(user.getDelayTime(),  delayDelta) );
        user.updateExecuteTime(safePlusInt(user.getExecuteTime(), execDelta) );
        user.updatePeanutCount(safePlusInt(user.getPeanutCount(), peanuts));

        incrementDelayBucket(user, stoppedAt);
        incrementFocusBucketsTouching(user, originalStart, stoppedAt, execDelta);

        return PlanDelayResponse.from(plan, delayDelta, execDelta, stoppedAt);
    }

    private long calcExpectedMinutes(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) return 0L;
        long m = ChronoUnit.MINUTES.between(start, end);
        return Math.max(m, 0L);
    }

    private static int safePlusInt(int current, int delta) {
        if (delta <= 0) return current;
        long sum = (long) current + (long) delta;
        return (sum > Integer.MAX_VALUE) ? Integer.MAX_VALUE : (int) sum;
    }

    private static int toNonNegativeInt(long minutes) {
        if (minutes <= 0) return 0;
        return (minutes > Integer.MAX_VALUE) ? Integer.MAX_VALUE : (int) minutes;
    }

    @Transactional
    public PlanDelayResponse delayAiPlan(Long aiPlanId, PlanDelayRequest request, User sessionUser) {
        if (request.category() != Category.AI && request.category() != Category.BASIC) {
            throw BaseException.type(PlanErrorCode.INVALID_CATEGORY);
        }

        if (request.actualStart() == null) {
            throw BaseException.type(PlanErrorCode.ACTUAL_START_REQUIRED);
        }
        if (request.executeTime() < 0) {
            throw BaseException.type(PlanErrorCode.INVALID_EXECUTE_MINUTES);
        }

        User user = userRepository.findWithSlotsById(sessionUser.getId())
                .orElseThrow(() -> new BaseException(PlanErrorCode.PLAN_NOT_FOUND));
        ensureSlotsInitialized(user);

        AiPlan aiPlan = aiPlanRepository.findById(aiPlanId)
                .filter(ap -> ap.getPlan() != null && ap.getPlan().getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new BaseException(AiPlanErrorCode.AIPLAN_NOT_FOUND));


        LocalDateTime originalStart = aiPlan.getScheduledStart(); // 예정 시작시각
        LocalDateTime originalEnd = aiPlan.getScheduledEnd();
        LocalDateTime stoppedAt = request.actualStart().plusMinutes(request.executeTime()); // 중지한 시각
        LocalDateTime newStart = request.newStartDateTime(); // 재시작 시각(요청 값)

        int delayDelta;
        if (originalStart != null && stoppedAt.isBefore(originalStart)) {
            delayDelta = 0;
        } else {
            long between = ChronoUnit.MINUTES.between(stoppedAt, newStart);
            delayDelta = toNonNegativeInt(between);
        }

        int execDelta;
        if (originalStart == null) {
            execDelta = 0;
        } else if (stoppedAt.isBefore(originalStart)) {
            execDelta = Math.max(request.executeTime(), 0);
        } else {
            long ran = ChronoUnit.MINUTES.between(originalStart, stoppedAt);
            execDelta = toNonNegativeInt(ran);
        }

        long expected = (aiPlan.getExpectedDuration() != null)
                ? aiPlan.getExpectedDuration()
                : calcExpectedMinutes(originalStart, originalEnd);
        long remaining = Math.max(0, expected - request.executeTime()); // 남은 예상 소요 시간

        int peanuts = calcPeanuts(request.executeTime(), expected);

        // 엔티티 업데이트
        aiPlan.updateScheduleStart(newStart);
        aiPlan.updateScheduleEnd(newStart.plusMinutes(remaining));
        aiPlan.updateExpectedDuration(remaining);
        aiPlan.updateStatus(Status.PAUSED);
        aiPlan.updateStoppedAt(stoppedAt);
        aiPlan.updateIsDelayed(true);

        user.updateDelayTime(safePlusInt(user.getDelayTime(),  delayDelta) );
        user.updateExecuteTime(safePlusInt(user.getExecuteTime(), execDelta) );
        user.updatePeanutCount(safePlusInt(user.getPeanutCount(), peanuts));

        incrementDelayBucket(user, stoppedAt);
        incrementFocusBucketsTouching(user, originalStart, stoppedAt, execDelta);

        return PlanDelayResponse.from(aiPlan, delayDelta, execDelta, stoppedAt);
    }

    @Transactional
    public PlanFinishResponse finishPlanOrAiPlan(Long planId, PlanFinishRequest request, User sessionUser) {
        log.info("[FINISH>REQ] planId={}, category={}, execMinutes={}, actualStart={}",
                planId, request.category(), request.executeTime(), request.actualStart());

        if (request.executeTime() < 0) {
            throw BaseException.type(PlanErrorCode.INVALID_EXECUTE_MINUTES);
        }
        if (request.actualStart() == null) {
            throw BaseException.type(PlanErrorCode.ACTUAL_START_REQUIRED);
        }

        int execDelta = request.executeTime();

        User user = userRepository.findWithSlotsById(sessionUser.getId())
                .orElseThrow(() -> new BaseException(UserErrorCode.USER_NOT_FOUND));
        log.info("[FINISH>USER] userId={}, delaySize={}, focusSize={}",
                user.getId(),
                user.getDelayList() == null ? null : user.getDelayList().size(),
                user.getFocusList() == null ? null : user.getFocusList().size());

        ensureSlotsInitialized(user);
        log.info("[FINISH>ENSURE] delaySize={}, focusSize={}",
                user.getDelayList().size(), user.getFocusList().size());


        if (request.category() == Category.BASIC) {
            Plan plan = planRepository.findByIdWithUserId(planId, user.getId())
                    .orElseThrow(() -> new BaseException(PlanErrorCode.PLAN_NOT_FOUND));

            long expectedMinutes = calcExpectedMinutes(plan.getScheduledStart(), plan.getScheduledEnd());
            int peanuts = calcPeanuts(execDelta, expectedMinutes);
            log.info("[FINISH>CALC] expectedMinutes={}, peanuts={}", expectedMinutes, peanuts);

            // 상태 변경
            plan.updateStatus(Status.FINISHED);

            int before = user.getExecuteTime();           // 원시 int, null 불가
            int after  = safePlusInt(before, execDelta);

            log.info("[FINISH>EXEC-TIME] before={}, delta={}, after={}", before, execDelta, after);

            // 집계
            user.updateExecuteTime(safePlusInt(user.getExecuteTime(), execDelta));
            addPeanuts(user, peanuts);

            log.info("[FINISH>EXEC-TIME] updatedEntityValue={}", user.getExecuteTime());

            // 집중 버킷 반영 (시작 시각 기준)
            LocalDateTime start = request.actualStart();
            LocalDateTime stop = request.actualStart().plusMinutes(execDelta);
            log.info("[FINISH>FOCUS] start={}, stop={}, execDelta={}", start, stop, execDelta);

            incrementFocusBucketsTouching(user, request.actualStart(), stop, execDelta);

            return PlanFinishResponse.from(plan, peanuts);

        } else if (request.category() == Category.AI) {
            AiPlan ai = aiPlanRepository.findById(planId)
                    .filter(ap -> ap.getPlan() != null && ap.getPlan().getUser().getId().equals(user.getId()))
                    .orElseThrow(() -> new BaseException(AiPlanErrorCode.AIPLAN_NOT_FOUND));

            long expectedMinutes = ai.getExpectedDuration() != null
                    ? ai.getExpectedDuration()
                    : calcExpectedMinutes(ai.getScheduledStart(), ai.getScheduledEnd());
            int peanuts = calcPeanuts(execDelta, expectedMinutes);
            log.info("[FINISH>CALC] expectedMinutes={}, peanuts={}", expectedMinutes, peanuts);

            ai.updateStatus(Status.FINISHED);

            user.updateExecuteTime(safePlusInt(user.getExecuteTime(), execDelta));
            addPeanuts(user, peanuts);

            LocalDateTime stop = request.actualStart().plusMinutes(execDelta);
            incrementFocusBucketsTouching(user, request.actualStart(), stop, execDelta);

            return PlanFinishResponse.from(ai, peanuts);
        }
        throw BaseException.type(PlanErrorCode.INVALID_CATEGORY);
    }

    private int nvl(Integer v) { return v == null ? 0 : v; }

    private void addPeanuts(User user, int delta) {
        if (delta <= 0) return;
        int cur = nvl(user.getPeanutCount());
        user.updatePeanutCount(safePlusInt(cur, delta));
    }

    private int calcPeanuts(long execMinutes, long expectedMinutes) {
        if (expectedMinutes <= 0) return 0;
        double ratio = (double) execMinutes / (double) expectedMinutes;
        if (ratio < 0.30) return 0;
        if (ratio < 0.65) return 1;
        if (ratio < 1.00) return 2;
        return 3;
    }
}
