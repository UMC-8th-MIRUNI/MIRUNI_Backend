package dgu.umc_app.domain.plan.dto.response;

import dgu.umc_app.domain.plan.entity.AiPlan;
import dgu.umc_app.domain.plan.entity.Plan;
import dgu.umc_app.domain.plan.entity.Category;
import io.swagger.v3.oas.annotations.media.Schema;

import static dgu.umc_app.global.common.DateTimeFormatUtil.formatDateTime;

public record UnstartedPlanResponse(
        Long id,
        String title,
        String scheduledStart,

        @Schema(description = "일정 유형(미루기 일정 판단)")
        Category category
) {
    public static UnstartedPlanResponse from(Plan plan) {
        return new UnstartedPlanResponse(
                plan.getId(),
                plan.getTitle(),
                formatDateTime(plan.getScheduledStart()),
                Category.BASIC
        );
    }

    public static UnstartedPlanResponse from(AiPlan aiPlan) {
        return new UnstartedPlanResponse(
                aiPlan.getId(),
                aiPlan.getDescription(),
                formatDateTime(aiPlan.getScheduledStart()),
                Category.AI
        );
    }

}
