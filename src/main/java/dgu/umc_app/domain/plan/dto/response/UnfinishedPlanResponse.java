package dgu.umc_app.domain.plan.dto.response;

import dgu.umc_app.domain.plan.entity.AiPlan;
import dgu.umc_app.domain.plan.entity.Plan;

import java.time.LocalDateTime;

public record UnfinishedPlanResponse(
        Long id,
        String title,
        LocalDateTime scheduledStart
) {
    public static UnfinishedPlanResponse from(Plan plan) {
        return new UnfinishedPlanResponse(
                plan.getId(),
                plan.getTitle(),
                plan.getScheduledStart()
        );
    }

    public static UnfinishedPlanResponse from(AiPlan aiPlan) {
        return new UnfinishedPlanResponse(
                aiPlan.getId(),
                aiPlan.getDescription(),
                aiPlan.getScheduledStart()
        );
    }

}
