package dgu.umc_app.domain.plan.service;

import dgu.umc_app.domain.plan.dto.response.*;
import dgu.umc_app.domain.plan.entity.PlanCategory;
import dgu.umc_app.domain.plan.exception.PlanErrorCode;
import dgu.umc_app.domain.plan.repository.AiPlanRepository;
import dgu.umc_app.domain.plan.entity.Plan;
import dgu.umc_app.domain.plan.entity.AiPlan;
import dgu.umc_app.domain.plan.repository.PlanRepository;
import dgu.umc_app.domain.user.entity.User;
import dgu.umc_app.global.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlanQueryService{

    private final PlanRepository planRepository;
    private final AiPlanRepository aiPlanRepository;

    public List<CalendarMonthResponse> getSchedulesByMonth(int year, int month, User user) {

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.atTime(23, 59, 59);

        Long userId = user.getId();
        List<Plan> plans = planRepository.findIndependentPlans(userId, year, month);
        List<AiPlan> aiPlans = aiPlanRepository.findByPlan_UserIdAndScheduledStartBetween(userId, startDateTime, endDateTime);

        Map<LocalDate, List<Boolean>> doneMap = new HashMap<>();

        for (Plan plan : plans) {
            LocalDateTime scheduledStart = plan.getScheduledStart();
            doneMap.computeIfAbsent(scheduledStart.toLocalDate(), k -> new ArrayList<>())
                    .add(plan.isDone());
        }

        for (AiPlan aiPlan : aiPlans) {
            LocalDateTime scheduledStart = aiPlan.getScheduledStart();
            doneMap.computeIfAbsent(scheduledStart.toLocalDate(), k -> new ArrayList<>())
                    .add(aiPlan.isDone());
        }

        return doneMap.entrySet().stream()
                .map(entry -> new CalendarMonthResponse(
                        entry.getKey(),
                        (int) entry.getValue().stream().filter(done -> !done).count(),  // 안 한 일정 갯수로 변경
                        entry.getValue().stream().allMatch(Boolean::booleanValue)
                ))
                .sorted(Comparator.comparing(CalendarMonthResponse::getDate))
                .collect(Collectors.toList());

    }

    public CalendarDayWrapperResponse getSchedulesByDate(LocalDate date, User user) {
        Long userId = user.getId();
        int year = date.getYear();
        int month = date.getMonthValue();

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);

        List<Plan> allIndependentPlans = planRepository.findIndependentPlans(userId, year, month);
        List<Plan> plans = allIndependentPlans.stream()
                .filter(plan -> {
                    LocalDateTime scheduledStart = plan.getScheduledStart();
                    return scheduledStart!=null && !plan.isDone()
                            && !scheduledStart.isBefore(startOfDay)
                            && !scheduledStart.isAfter(endOfDay);
                })
                .toList();

        List<AiPlan> aiPlans = aiPlanRepository.findByPlan_UserIdAndScheduledStartBetween(userId, startOfDay, endOfDay)
                .stream()
                .filter(aiPlan -> !aiPlan.isDone())
                .toList();

        List<CalendarDayResponse> result = new ArrayList<>();

        plans.forEach(plan -> result.add(CalendarDayResponse.from(plan)));
        aiPlans.forEach(aiPlan -> result.add(CalendarDayResponse.from(aiPlan)));

        result.sort(Comparator.comparing(CalendarDayResponse::startTime, Comparator.nullsLast(Comparator.naturalOrder())));

        int totalCount = (int) plans.stream().filter(plan -> !plan.isDone()).count()
                + (int) aiPlans.stream().filter(aiPlan -> !aiPlan.isDone()).count();    // 안 한 일정 갯수로 변경
        return new CalendarDayWrapperResponse(result.size(), result);
    }

    public List<DelayedPlanResponse> getDelayedPlans(User user) {
        Long userId = user.getId();

        List<Plan> delayedPlans = planRepository.findByUserIdAndIsDelayedTrue(userId);
        List<AiPlan> delayedAiPlans = aiPlanRepository.findByPlan_UserIdAndIsDelayedTrue(userId);

        List<DelayedPlanResponse> result = new ArrayList<>();
        delayedPlans.forEach(plan -> result.add(DelayedPlanResponse.from(plan)));
        delayedAiPlans.forEach(aiPlan -> result.add(DelayedPlanResponse.from(aiPlan)));

        return result;
    }

    public List<UnfinishedPlanResponse> getUnfinishedPlans(User user) {
        Long userId = user.getId();

        LocalDateTime yesterday = LocalDate.now().minusDays(1).atTime(23, 59, 59);

        List<Plan> unfinishedPlans = planRepository.findByUserIdAndIsDoneFalseAndIsDelayedFalse(userId)
                .stream()
                .filter(plan -> plan.getScheduledEnd().isBefore(yesterday))
                .toList();

        List<AiPlan> unfinishedAiPlans = aiPlanRepository.findByPlan_UserIdAndIsDoneFalseAndIsDelayedFalse(userId)
                .stream()
                .filter(aiPlan -> aiPlan.getScheduledEnd().isBefore(yesterday))
                .toList();

        List<UnfinishedPlanResponse> result = new ArrayList<>();

        unfinishedPlans.forEach(plan -> result.add(UnfinishedPlanResponse.from(plan)));
        unfinishedAiPlans.forEach(aiPlan -> result.add(UnfinishedPlanResponse.from(aiPlan)));

        return result;
    }

    public PlanDetailResponse getPlanDetail(Long planId, Long userId) {
        // 1. Plan 조회
        Plan plan = planRepository.findByIdAndUserId(planId, userId)
                .orElseThrow(() -> new BaseException(PlanErrorCode.PLAN_NOT_FOUND));

        // 2. 해당 Plan이 AI 일정인지 확인
        List<AiPlan> aiPlans = aiPlanRepository.findByPlanId(planId);

        if (!aiPlans.isEmpty()) {
            // AI 일정이면
            return PlanDetailResponse.fromAiPlan(plan, aiPlans);
        }

        // 일반 일정이면
        return PlanDetailResponse.fromPlan(plan);
    }

}
