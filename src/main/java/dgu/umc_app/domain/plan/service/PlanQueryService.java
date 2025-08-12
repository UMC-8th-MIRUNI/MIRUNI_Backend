package dgu.umc_app.domain.plan.service;

import dgu.umc_app.domain.plan.dto.response.*;
import dgu.umc_app.domain.plan.entity.Status;
import dgu.umc_app.domain.plan.exception.PlanErrorCode;
import dgu.umc_app.domain.plan.repository.AiPlanRepository;
import dgu.umc_app.domain.plan.entity.Plan;
import dgu.umc_app.domain.plan.entity.AiPlan;
import dgu.umc_app.domain.plan.repository.PlanRepository;
import dgu.umc_app.domain.user.entity.User;
import dgu.umc_app.domain.user.exception.UserErrorCode;
import dgu.umc_app.domain.user.repository.UserRepository;
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
    private final UserRepository userRepository;

    public List<CalendarMonthResponse> getSchedulesByMonth(int year, int month, User user) {

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.atTime(23, 59, 59);

        Long userId = user.getId();
        List<Plan> plans = planRepository.findIndependentPlans(userId, year, month);
        List<AiPlan> aiPlans = aiPlanRepository.findByPlan_UserIdAndScheduledStartBetween(userId, startDateTime, endDateTime);

        Map<LocalDate, List<Status>> statusMap = new HashMap<>();

        for (Plan plan : plans) {
            LocalDateTime scheduledStart = plan.getScheduledStart();
            Status status = plan.getStatus();
            if (status != null) {
                statusMap.computeIfAbsent(scheduledStart.toLocalDate(), k -> new ArrayList<>() ).add(status);
            }
        }

        for (AiPlan aiPlan : aiPlans) {
            LocalDateTime scheduledStart = aiPlan.getScheduledStart();
            Status status = aiPlan.getStatus();
            if (status != null) {
                statusMap.computeIfAbsent(scheduledStart.toLocalDate(), k -> new ArrayList<>()).add(status);
            }
        }

        return statusMap.entrySet().stream()
                .map(e -> {
                        long pendingcount = e.getValue().stream()
                                .filter(s -> s == Status.NOT_STARTED || s == Status.PAUSED)
                                .count();
                        return new CalendarMonthResponse(e.getKey(), (int) pendingcount);
                })
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
                    return scheduledStart!=null
                            && (plan.getStatus() == Status.NOT_STARTED || plan.getStatus() == Status.PAUSED)
                            && !scheduledStart.isBefore(startOfDay)
                            && !scheduledStart.isAfter(endOfDay);
                })
                .toList();

        List<AiPlan> aiPlans = aiPlanRepository.findByPlan_UserIdAndScheduledStartBetween(userId, startOfDay, endOfDay)
                .stream()
                .filter(aiPlan -> aiPlan.getStatus() == Status.NOT_STARTED || aiPlan.getStatus() == Status.PAUSED)
                .toList();

        List<CalendarDayResponse> result = new ArrayList<>();
        plans.forEach(plan -> result.add(CalendarDayResponse.from(plan)));
        aiPlans.forEach(aiPlan -> result.add(CalendarDayResponse.from(aiPlan)));

        result.sort(Comparator.comparing(CalendarDayResponse::startTime, Comparator.nullsLast(Comparator.naturalOrder())));

        return new CalendarDayWrapperResponse(result.size(), result);
    }

    public List<DelayedPlanResponse> getDelayedPlans(User user) {
        Long userId = user.getId();

        List<Plan> delayedPlans = planRepository.findByUserIdAndStatus(userId, Status.PAUSED);
        List<AiPlan> delayedAiPlans = aiPlanRepository.findByPlan_UserIdAndStatus(userId, Status.PAUSED);

        List<DelayedPlanResponse> result = new ArrayList<>();
        delayedPlans.forEach(plan -> result.add(DelayedPlanResponse.from(plan)));
        delayedAiPlans.forEach(aiPlan -> result.add(DelayedPlanResponse.from(aiPlan)));

        return result;
    }

    public List<UnstartedPlanResponse> getUnstartedPlans(User user) {
        Long userId = user.getId();

        LocalDateTime yesterday = LocalDate.now().minusDays(1).atTime(23, 59, 59);

        List<Plan> unstartedPlans = planRepository.findByUserIdAndStatus(userId, Status.NOT_STARTED)
                .stream()
                .filter(plan -> plan.getScheduledEnd().isBefore(yesterday))
                .toList();

        List<AiPlan> unstartedAiPlans = aiPlanRepository.findByPlan_UserIdAndStatus(userId, Status.NOT_STARTED)
                .stream()
                .filter(aiPlan -> aiPlan.getScheduledEnd().isBefore(yesterday))
                .toList();

        List<UnstartedPlanResponse> result = new ArrayList<>();

        unstartedPlans.forEach(plan -> result.add(UnstartedPlanResponse.from(plan)));
        unstartedAiPlans.forEach(aiPlan -> result.add(UnstartedPlanResponse.from(aiPlan)));

        return result;
    }

    public HomeResponse getHomePage(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> BaseException.type(UserErrorCode.USER_NOT_FOUND));

        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.atTime(23, 59, 59);

        List<Plan> plans = planRepository.findByUserIdAndScheduledStartBetween(userId, start, end);

        // TODO isDone 상태 세가지로 나눈거 반영
        int totalCount = plans.size();
        int completedCount = (int) plans.stream().filter(Plan::isDone).count();
        int pausedCount = (int) plans.stream().filter(Plan::isDelayed).count();
        int scheduledCount = totalCount - completedCount - pausedCount;

        int achievementRate = (totalCount == 0) ? 0 : (int) Math.round((completedCount * 100.0) / totalCount);

        List<HomeResponse.TaskInfo> tasks = plans.stream()
                .sorted(
                        Comparator
                                .comparing(Plan::isDone)
                                .thenComparing(Plan::getScheduledStart)
                )
                .map(HomeResponse.TaskInfo::from)
                .toList();

        return HomeResponse.of(user, totalCount, scheduledCount, pausedCount, completedCount, achievementRate, tasks);
    }

    public PlanDetailResponse getPlanDetail(Long planId, Long userId) {
        // 1. Plan 조회
        Plan plan = planRepository.findByIdWithUserId(planId, userId)
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
