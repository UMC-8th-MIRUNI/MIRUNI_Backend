package dgu.umc_app.domain.plan.dto.response;

import dgu.umc_app.domain.plan.dto.request.BasicDetailUpdate;
import dgu.umc_app.domain.plan.entity.AiPlan;
import dgu.umc_app.domain.plan.entity.Category;
import dgu.umc_app.domain.plan.entity.Plan;
import dgu.umc_app.domain.plan.entity.Priority;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public record PlanDetailResponse(

        @Schema(example = "BASIC")
        @NotNull
        Category category,

        @Schema(description = "일정 제목")
        String title,

        @Schema(description = "마감기한", example = "2025-08-30T23:59:59")
        LocalDateTime deadline,

        @Schema(description = "우선 순위")
        Priority priority,

        @Schema(description = "일반 세부일정 리스트")
        List<PlanDetail> plans
) {
    public static PlanDetailResponse fromPlan(Plan plan) {
        long expectedDuration = Duration.between(
                plan.getScheduledStart().toLocalTime(),
                plan.getScheduledEnd().toLocalTime()
        ).toMinutes();

        return new PlanDetailResponse(
                Category.BASIC,
                plan.getTitle(),
                plan.getDeadline(),
                plan.getPriority(),
                List.of(new PlanDetail(
                        plan.getId(),
                        plan.getScheduledStart().toLocalDate(),
                        plan.getDescription(),
                        plan.getScheduledStart().toLocalTime(),
                        plan.getScheduledEnd().toLocalTime()
                ))
        );
    }
}
