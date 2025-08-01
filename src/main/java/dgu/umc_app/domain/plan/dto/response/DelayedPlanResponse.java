package dgu.umc_app.domain.plan.dto.response;

import dgu.umc_app.domain.plan.entity.AiPlan;
import dgu.umc_app.domain.plan.entity.Plan;

import java.time.LocalDateTime;

public record DelayedPlanResponse (
        Long id,
        String title,
        LocalDateTime executeDate
){
    public static DelayedPlanResponse from(Plan plan) {
        return new DelayedPlanResponse(
                plan.getId(),
                plan.getTitle(),
                plan.getExecuteDate()
        );
    }

    public static DelayedPlanResponse from(AiPlan aiPlan) {
        return new DelayedPlanResponse(
                aiPlan.getId(),
                aiPlan.getDescription(),
                aiPlan.getScheduledDate().atStartOfDay()
        );
    }
}
