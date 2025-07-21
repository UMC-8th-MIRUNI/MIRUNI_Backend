package dgu.umc_app.domain.plan.dto;

import dgu.umc_app.domain.ai_plan.entity.AiPlan;
import dgu.umc_app.domain.plan.entity.Plan;


public record CalendarDayResponse (
    Long id,        // 일정 ID (Plan or AIPlan)
    String title,    // 제목 (Plan.title 또는 AiPlan.description)
    boolean isDone,  // 완료 여부
    String type        // NORMAL or AI_SPLIT
){

    public static CalendarDayResponse from(Plan plan) {
        return new CalendarDayResponse(
                plan.getId(),
                plan.getTitle(),
                plan.isDone(),
                "NORMAL"
        );
    }

    public static CalendarDayResponse from(AiPlan aiPlan) {
        return new CalendarDayResponse(
                aiPlan.getId(),
                aiPlan.getDescription(),
                aiPlan.isDone(),
                "AI_SPLIT"
        );
    }

}