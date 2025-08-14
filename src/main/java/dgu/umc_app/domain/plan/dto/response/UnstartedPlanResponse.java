package dgu.umc_app.domain.plan.dto.response;

import dgu.umc_app.domain.plan.entity.AiPlan;
import dgu.umc_app.domain.plan.entity.Plan;
import dgu.umc_app.domain.plan.entity.Category;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record UnstartedPlanResponse(
        Long id,
        String title,
        LocalDateTime scheduledStart,

        @Schema(description = "일정 유형(미루기 일정 판단)")
        Category category
) {
    public static UnstartedPlanResponse from(Plan plan) {
        return new UnstartedPlanResponse(
                plan.getId(),
                plan.getTitle(),
                plan.getScheduledStart(),
                Category.BASIC
        );
    }

    public static UnstartedPlanResponse from(AiPlan aiPlan) {
        return new UnstartedPlanResponse(
                aiPlan.getId(),
                aiPlan.getDescription(),
                aiPlan.getScheduledStart(),
                Category.AI
        );
    }

}
