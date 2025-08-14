package dgu.umc_app.domain.plan.service;

import dgu.umc_app.domain.plan.dto.request.*;
import dgu.umc_app.domain.plan.dto.response.*;
import dgu.umc_app.domain.plan.entity.*;
import dgu.umc_app.domain.plan.exception.AiPlanErrorCode;
import dgu.umc_app.domain.plan.exception.PlanErrorCode;
import dgu.umc_app.domain.plan.repository.AiPlanRepository;
import dgu.umc_app.domain.plan.repository.PlanRepository;
import dgu.umc_app.domain.user.entity.User;
import dgu.umc_app.domain.user.repository.UserRepository;
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
import java.util.Map;

@Service
@RequiredArgsConstructor
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
                plan.getScheduledEnd(),
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

    private void assertTimeOrder(LocalDateTime start, LocalDateTime end) {
        if (start != null && end != null && end.isBefore(start)) {
            throw BaseException.type(PlanErrorCode.INVALID_DATE_RANGE);
        }
    }

    @Transactional
    public UpdateResponse updateSchedule(Long planId, ScheduleUpdateRequest req, User user) {
        if (req instanceof PlanUpdateRequest r) {
            return updateBasicPlan(planId, r, user);
        } else if (req instanceof AiPlanUpdateRequest r) {
            return updateAiPlan(planId, r, user);
        } else {
            throw new IllegalArgumentException("Unknown request type: " + req.getClass());
        }
    }
    private static <T> T nvl(T v, T fallback) { return v != null ? v : fallback; }

    private static void assertWithinDeadline(LocalDateTime deadline, LocalDateTime end) {
        if (deadline != null && end != null && end.isAfter(deadline)) {
            throw BaseException.type(AiPlanErrorCode.AFTER_DEADLINE);
        }
    }
    private void assertStartEndRequired(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            throw BaseException.type(PlanErrorCode.INVALID_DATE_RANGE); // "시작/종료 시간은 필수" 등 전용 코드 권장
        }
    }

    private void assertNotBeforeToday(LocalDateTime dt, PlanErrorCode code) {
        if (dt != null && dt.isBefore(LocalDateTime.now())) {
            throw BaseException.type(code);
        }
    }

    @Transactional
    public UpdateResponse updateBasicPlan(Long planId, PlanUpdateRequest req, User user) {

        Plan base = planRepository.findByIdWithUserId(planId, user.getId())
                .orElseThrow(() -> BaseException.type(PlanErrorCode.PLAN_NOT_FOUND));

        if (req.title() != null)    base.updateTitle(req.title());
        if (req.priority() != null) base.updatePriority(req.priority());
        if (req.deadline() != null){
            assertNotBeforeToday(req.deadline(), PlanErrorCode.PAST_DEADLINE_NOT_ALLOWED);
            base.updateDeadline(req.deadline());
        }
        if (req.description() != null) base.updateDescription(req.description());

        LocalDateTime newStart = base.getScheduledStart();
        LocalDateTime newEnd   = base.getScheduledEnd();

        assertTimeOrder(newStart, newEnd);
        assertWithinDeadline(nvl(req.deadline(), base.getDeadline()), newEnd);
        assertNotBeforeToday(newStart, PlanErrorCode.PAST_START_NOT_ALLOWED);
        assertNotBeforeToday(newEnd,   PlanErrorCode.PAST_END_NOT_ALLOWED);

        base.updateScheduledStart(newStart);
        base.updateScheduledEnd(newEnd);

        base.touch();
        return UpdateResponse.fromPlan(planId, base.getUpdatedAt());
    }

    @Transactional
    public UpdateResponse updateAiPlan(Long planId, AiPlanUpdateRequest req, User user) {

        // 상위 일정 조회 먼저
        Plan plan = planRepository.findByIdWithUserId(planId, user.getId())
                .orElseThrow(() -> BaseException.type(PlanErrorCode.PLAN_NOT_FOUND));

        if (req.title() != null)     plan.updateTitle(req.title());
        if (req.priority() != null)  plan.updatePriority(req.priority());
        if (req.deadline() != null){
            assertNotBeforeToday(req.deadline(), PlanErrorCode.PAST_DEADLINE_NOT_ALLOWED);
            plan.updateDeadline(req.deadline());
        }

        Map<Long, AiPlan> existing = plan.getAiPlans().stream()
                .collect(java.util.stream.Collectors.toMap(AiPlan::getId, java.util.function.Function.identity()));


        PlanType basePlanType = plan.getAiPlans().stream()
                .map(AiPlan::getPlanType)
                .filter(java.util.Objects::nonNull)
                .findFirst()
                .orElse(PlanType.IMMERSIVE);

        int order = 1;
        if (req.plans() != null) {
            for (AiDetailUpdate d : req.plans()) {
//                // 삭제
//                if (Boolean.TRUE.equals(d.delete())) {
//                    if (d.aiPlanId() != null && existing.containsKey(d.aiPlanId())) {
//                        plan.removeAiPlan(existing.get(d.aiPlanId()));
//                    }
//                    continue;
//                }

                // 시간 계산
                LocalDateTime start = (d.date() != null && d.startTime() != null)
                        ? LocalDateTime.of(d.date(), d.startTime())
                        : null;
                LocalDateTime end = (d.date() != null && d.endTime() != null)
                        ? LocalDateTime.of(d.date(), d.endTime())
                        : null;

                // 신규 생성
                if (d.aiPlanId() == null) {
                    assertNotBeforeToday(start, PlanErrorCode.PAST_START_NOT_ALLOWED);
                    assertNotBeforeToday(end,   PlanErrorCode.PAST_END_NOT_ALLOWED);
                    assertStartEndRequired(start, end);
                    assertTimeOrder(start, end);
                    assertWithinDeadline(plan.getDeadline(), end);

                    AiPlan ai = AiPlan.builder()
                            .plan(plan)
                            .stepOrder((long) order++)
                            .priority(req.priority() != null ? req.priority() : plan.getPriority())
                            .planType(basePlanType)
                            .taskRange(req.taskRange())
                            .description(d.description())
                            .expectedDuration(d.expectedDuration())
                            .scheduledStart(start)
                            .scheduledEnd(end)
                            .status(Status.NOT_STARTED)
                            .isDelayed(false)
                            .build();

                    plan.addAiPlan(ai);
                    continue;
                }
                // 수정
                AiPlan target = existing.get(d.aiPlanId());
                if (target == null) continue;

                // 부분 수정 허용(널은 유지)
                if (d.description() != null) target.updateDescription(d.description());
                if (d.expectedDuration() != null) target.updateExpectedDuration(d.expectedDuration());
                if (start != null) target.updateScheduleStart(start);
                if (end != null) target.updateScheduleEnd(end);
                if (req.priority() != null) target.updatePriority(req.priority());
                if (req.taskRange() != null) target.updateTaskRange(req.taskRange());
                // planType은 유지

                assertNotBeforeToday(target.getScheduledStart(), PlanErrorCode.PAST_START_NOT_ALLOWED);
                assertNotBeforeToday(target.getScheduledEnd(),   PlanErrorCode.PAST_END_NOT_ALLOWED);
                assertTimeOrder(target.getScheduledStart(), target.getScheduledEnd());
                assertWithinDeadline(plan.getDeadline(), target.getScheduledEnd());
            }

            plan.getAiPlans().sort(java.util.Comparator.comparing(AiPlan::getScheduledStart));
            for (int i = 0; i < plan.getAiPlans().size(); i++) {
                plan.getAiPlans().get(i).updateStepOrder((long) (i + 1));
            }
        }
            plan.touch();
            return UpdateResponse.fromPlan(plan.getId(), plan.getUpdatedAt());
    }

    @Transactional
    public PlanDelayResponse delayPlan(Long planId, PlanDelayRequest request, User sessionUser) {
        if (request.category() != Category.AI && request.category() != Category.BASIC) {
            throw BaseException.type(PlanErrorCode.INVALID_CATEGORY);
        }

        User user = userRepository.findWithSlotsById(sessionUser.getId())
                .orElseThrow(() -> new BaseException(PlanErrorCode.PLAN_NOT_FOUND));
        ensureSlotsInitialized(user);

        Plan plan = planRepository.findByIdWithUserId(planId, user.getId())
                .orElseThrow(() -> new BaseException(PlanErrorCode.PLAN_NOT_FOUND));

        if (request.expectedMinutes() == null) {
            throw BaseException.type(AiPlanErrorCode.EXPECTED_MINUTES_REQUIRED);
        }

        LocalDateTime stoppedAt = LocalDateTime.now(); // 중지 시각
        LocalDateTime newStart = request.newStartDateTime(); // 재시작 시각(요청 값)
        LocalDateTime originalStart = plan.getScheduledStart(); // 예정 시작시각

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
        plan.updateScheduleEnd(newStart.plusMinutes(request.expectedMinutes()));
        plan.updateStatus(Status.PAUSED);
        plan.updateStoppedAt(stoppedAt);

        user.updateDelayTimes(safePlusInt(user.getDelayTime(),  delayDelta) );
        user.updateExecuteTimes(safePlusInt(user.getExecuteTime(), execDelta) );

        incrementDelayBucket(user, stoppedAt);
        incrementFocusBucketsTouching(user, originalStart, stoppedAt, execDelta);

        return PlanDelayResponse.from(plan, delayDelta, execDelta, stoppedAt);
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

        User user = userRepository.findWithSlotsById(sessionUser.getId())
                .orElseThrow(() -> new BaseException(PlanErrorCode.PLAN_NOT_FOUND));
        ensureSlotsInitialized(user);

        AiPlan aiPlan = aiPlanRepository.findById(aiPlanId)
                .filter(ap -> ap.getPlan() != null && ap.getPlan().getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new BaseException(AiPlanErrorCode.AIPLAN_NOT_FOUND));

        if (request.expectedMinutes() == null) {
            throw BaseException.type(AiPlanErrorCode.EXPECTED_MINUTES_REQUIRED);
        }

        LocalDateTime stoppedAt = LocalDateTime.now();
        LocalDateTime newStart = request.newStartDateTime();
        LocalDateTime originalStart = aiPlan.getScheduledStart();

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

        user.updateDelayTimes(safePlusInt(user.getDelayTime(),  delayDelta) );
        user.updateExecuteTimes(safePlusInt(user.getExecuteTime(), execDelta) );

        incrementDelayBucket(user, stoppedAt);
        incrementFocusBucketsTouching(user, originalStart, stoppedAt, execDelta);

        return PlanDelayResponse.from(aiPlan, delayDelta, execDelta, stoppedAt);
    }

}
