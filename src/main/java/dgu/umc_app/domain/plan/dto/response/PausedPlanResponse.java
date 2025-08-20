package dgu.umc_app.domain.plan.dto.response;

import dgu.umc_app.domain.plan.entity.AiPlan;
import dgu.umc_app.domain.plan.entity.Plan;
import dgu.umc_app.domain.plan.entity.Category;
import dgu.umc_app.domain.plan.entity.Priority;
import io.swagger.v3.oas.annotations.media.Schema;

import static dgu.umc_app.global.common.DateTimeFormatUtil.formatDateTime;

public record PausedPlanResponse(
        Long id,
        String scheduledStart,
        String title,
        String description,
        Priority priority,

        @Schema(description = "일정 유형(미루기 일정 판단)")
        Category category
){
    public static PausedPlanResponse from(Plan plan) {
        return new PausedPlanResponse(
                plan.getId(),
                formatDateTime(plan.getScheduledStart()),
                plan.getTitle(),
                plan.getDescription(),
                plan.getPriority(),
                Category.BASIC
        );
    }

    public static PausedPlanResponse from(AiPlan aiPlan) {
        return new PausedPlanResponse(
                aiPlan.getId(),
                formatDateTime(aiPlan.getScheduledStart()),
                aiPlan.getPlan().getTitle(),
                aiPlan.getDescription(),
                aiPlan.getPriority(),
                Category.AI
        );
    }
}
