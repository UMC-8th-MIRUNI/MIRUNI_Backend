package dgu.umc_app.domain.plan.dto.response;

import dgu.umc_app.domain.plan.entity.AiPlan;
import dgu.umc_app.domain.plan.entity.Category;
import dgu.umc_app.domain.plan.entity.Plan;
import dgu.umc_app.domain.plan.entity.Priority;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record PlanDetailResponse(

        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "BASIC")
        @NotNull
        Category category,

        @Schema(description = "일정 제목")
        String title,

        @Schema(description = "마감기한", example = "2025-08-30T23:59:59")
        LocalDateTime deadline,

        @Schema(description = "우선 순위")
        Priority priority,

        @Schema(description = "실행시작날짜")
        LocalDateTime scheduledStart,

        @Schema(description = "실행종료날짜")
        LocalDateTime scheduledEnd,

        @Schema(description = "일정 간단 설명")
        String description
) implements ScheduleDetailResponse{
    public static PlanDetailResponse fromPlan(Plan plan) {
        return new PlanDetailResponse(
                Category.BASIC,
                plan.getTitle(),
                plan.getDeadline(),
                plan.getPriority(),
                plan.getScheduledStart(),
                plan.getScheduledEnd(),
                plan.getDescription()
        );
    }
}
