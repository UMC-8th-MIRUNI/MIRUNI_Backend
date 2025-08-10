package dgu.umc_app.domain.plan.service;

import dgu.umc_app.domain.plan.dto.response.*;
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
}
