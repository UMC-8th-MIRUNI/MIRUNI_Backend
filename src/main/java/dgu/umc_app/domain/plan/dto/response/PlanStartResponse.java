package dgu.umc_app.domain.plan.dto.response;

import dgu.umc_app.domain.plan.entity.AiPlan;
import dgu.umc_app.domain.plan.entity.Plan;
import dgu.umc_app.domain.plan.entity.Status;
import io.swagger.v3.oas.annotations.media.Schema;

public record PlanStartResponse(

        @Schema
        Long planId,

        @Schema
        Long aiPlanId,

        @Schema
        Status status
) {
    public static PlanStartResponse fromPlan(Plan plan) {
        return new PlanStartResponse(plan.getId(), null, plan.getStatus());
    }
    public static PlanStartResponse fromAiPlan(AiPlan aiPlan) {
        return new PlanStartResponse(aiPlan.getPlan().getId(), aiPlan.getId(), aiPlan.getStatus());
    }
}
