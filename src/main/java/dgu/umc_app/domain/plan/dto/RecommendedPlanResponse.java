package dgu.umc_app.domain.plan.dto;

import dgu.umc_app.domain.plan.entity.Plan;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

public record RecommendedPlanResponse(
        @Schema(example = "3")
        Long id,

        @Schema(example = "UMC 부스 준비하기")
        String title,

        @Schema(example = "2025-07-06")
        LocalDate deadline
) {
    public static RecommendedPlanResponse from(Plan plan) {
        return new RecommendedPlanResponse(
                plan.getId(),
                plan.getTitle(),
                plan.getDeadline().toLocalDate()
        );
    }
}
