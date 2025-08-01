package dgu.umc_app.domain.plan.service;

import dgu.umc_app.domain.plan.entity.PlanCategory;
import dgu.umc_app.domain.plan.repository.AiPlanRepository;
import dgu.umc_app.domain.plan.dto.CalendarDayResponse;
import dgu.umc_app.domain.plan.dto.CalendarDayWrapperResponse;
import dgu.umc_app.domain.plan.dto.CalendarMonthResponse;
import dgu.umc_app.domain.plan.dto.DelayedPlanResponse;
import dgu.umc_app.domain.plan.entity.Plan;
import dgu.umc_app.domain.plan.entity.AiPlan;
import dgu.umc_app.domain.plan.repository.PlanRepository;
import dgu.umc_app.domain.user.entity.User;
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

    public List<CalendarMonthResponse> getSchedulesByMonth(int year, int month, User user) {

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.atTime(23, 59, 59);

        Long userId = user.getId();
        List<Plan> plans = planRepository
                .findByUserIdAndExecuteDateBetween(userId, startDateTime, endDateTime)
                .stream()
                .filter(plan -> plan.getPlanCategory() == PlanCategory.BASIC)
                .toList();

        List<AiPlan> aiPlans = aiPlanRepository.findByPlan_UserIdAndScheduledDateBetween(userId, start, end);

        Map<LocalDate, List<Boolean>> doneMap = new HashMap<>();

        for (Plan plan : plans) {
            LocalDateTime executeDate = plan.getExecuteDate();
            doneMap.computeIfAbsent(executeDate.toLocalDate(), k -> new ArrayList<>())
                    .add(plan.isDone());
        }

        for (AiPlan aiPlan : aiPlans) {
            LocalDate scheduleDate = aiPlan.getScheduledDate();
            doneMap.computeIfAbsent(scheduleDate, k -> new ArrayList<>())
                    .add(aiPlan.isDone());
        }

        return doneMap.entrySet().stream()
                .map(entry -> new CalendarMonthResponse(
                        entry.getKey(),
                        entry.getValue().size(),
                        entry.getValue().stream().allMatch(Boolean::booleanValue)
                ))
                .sorted(Comparator.comparing(CalendarMonthResponse::getDate))
                .collect(Collectors.toList());

    }

    public CalendarDayWrapperResponse getSchedulesByDate(LocalDate date, User user) {
        Long userId = user.getId();

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);

        List<Plan> plans = planRepository
                .findByUserIdAndExecuteDateBetween(userId, startOfDay, endOfDay)
                .stream()
                .filter(plan -> plan.getPlanCategory() == PlanCategory.BASIC)
                .toList();
        List<AiPlan> aiPlans = aiPlanRepository.findByPlan_UserIdAndScheduledDate(userId, date);

        List<CalendarDayResponse> result = new ArrayList<>();

        for (Plan plan : plans) {
            result.add(CalendarDayResponse.from(plan));
        }

        for (AiPlan aiPlan : aiPlans) {
            result.add(CalendarDayResponse.from(aiPlan));
        }

        result.sort(Comparator.comparing(CalendarDayResponse::startTime, Comparator.nullsLast(Comparator.naturalOrder())));

        int totalCount = plans.size() + aiPlans.size();

        return new CalendarDayWrapperResponse(totalCount, result);
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

}

