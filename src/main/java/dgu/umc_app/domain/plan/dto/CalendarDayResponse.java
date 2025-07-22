package dgu.umc_app.domain.plan.dto;

import dgu.umc_app.domain.ai_plan.entity.AiPlan;
import dgu.umc_app.domain.plan.entity.Plan;

import java.time.LocalTime;

public record CalendarDayResponse (
    Long id,        // 일정 ID (Plan or AIPlan)
    String title,    // 제목 (Plan.title 또는 AiPlan.description)
    boolean isDone,  // 완료 여부
    String type,        // NORMAL or AI_SPLIT
    LocalTime startTime,    // 각 일정의 시작 시간
    LocalTime endTime   // 각 일정의 종료 시간
){

    public static CalendarDayResponse from(Plan plan) {
        return new CalendarDayResponse(
                plan.getId(),
                plan.getTitle(),
                plan.isDone(),
                "NORMAL",
                plan.getStartTime(),
                plan.getEndTime()
        );
    }

    public static CalendarDayResponse from(AiPlan aiPlan) {
        return new CalendarDayResponse(
                aiPlan.getId(),
                aiPlan.getDescription(),
                aiPlan.isDone(),
                "AI_SPLIT",
                aiPlan.getStartTime(),
                aiPlan.getEndTime()
        );
    }
}
