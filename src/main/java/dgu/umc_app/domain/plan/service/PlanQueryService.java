package dgu.umc_app.domain.plan.service;

import dgu.umc_app.domain.ai_plan.repository.AiPlanRepository;
import dgu.umc_app.domain.plan.dto.CalendarDayResponse;
import dgu.umc_app.domain.plan.dto.CalendarMonthResponse;
import dgu.umc_app.domain.plan.entity.Plan;
import dgu.umc_app.domain.ai_plan.entity.AiPlan;
import dgu.umc_app.domain.plan.repository.PlanRepository;
import dgu.umc_app.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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

        Long userId = user.getId();
        List<Plan> plans = planRepository.findByUserIdAndDeadlineBetween(userId, start, end);
        List<AiPlan> aiPlans = aiPlanRepository.findByPlan_UserIdAndScheduledDateBetween(userId, start, end);

        // 날짜 별로 일정들을 그룹핑
        Map<LocalDate, List<CalendarMonthResponse.ScheduleInfo>> grouped = new HashMap<>();

        for (Plan plan : plans) {
            grouped.computeIfAbsent(plan.getDeadline(), k -> new ArrayList<>())
                    .add(new CalendarMonthResponse.ScheduleInfo(
                            plan.getId(),
                            plan.getTitle(),
                            plan.isDone(),
                            "NORMAL"));
        }

        for (AiPlan aiPlan : aiPlans) {
            grouped.computeIfAbsent(aiPlan.getScheduledDate(), k -> new ArrayList<>())
                    .add(new CalendarMonthResponse.ScheduleInfo(
                            aiPlan.getId(),
                            aiPlan.getPlan().getTitle(),
                            aiPlan.isDone(),
                            "AI_SPLIT"));
        }

        return grouped.entrySet().stream()
                .map(entry -> new CalendarMonthResponse(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(CalendarMonthResponse::getDeadline))
                .collect(Collectors.toList());
    }

    public List<CalendarDayResponse> getSchedulesByDate(LocalDate date, User user) {
        Long userId = user.getId();
        List<Plan> plans = planRepository.findByUserIdAndDeadline(userId, date);
        List<AiPlan> aiPlans = aiPlanRepository.findByPlan_UserIdAndScheduledDate(userId, date);

        List<CalendarDayResponse> result = new ArrayList<>();
        plans.forEach(plan -> result.add(CalendarDayResponse.from(plan)));
        aiPlans.forEach(aiPlan -> result.add(CalendarDayResponse.from(aiPlan)));

        return result;
    }
}

