package dgu.umc_app.domain.plan.dto.response;

import dgu.umc_app.domain.plan.entity.AiPlan;
import dgu.umc_app.domain.plan.entity.Plan;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public record PlanDetailResponse(

        @Schema(description = "일정 제목")
        String title,

        @Schema(description = "마감기한")
        LocalDateTime deadline,

        @Schema(description = "일정 범위")
        String taskRange,

        @Schema(description = "우선 순위")
        String priority,

        @Schema(description = "상세 일정 리스트")
        List<PlanDetail> plans
) {
    public static PlanDetailResponse fromPlan(Plan plan) {
        long expectedDuration = Duration.between(
                plan.getScheduledStart().toLocalTime(),
                plan.getScheduledEnd().toLocalTime()
        ).toMinutes();

        return new PlanDetailResponse(
                plan.getTitle(),
                plan.getDeadline(),
                "", // 일반일정은 range 없음
                plan.getPriority().name(),
                List.of(new PlanDetail(
                        plan.getScheduledStart().toLocalDate(),
                        plan.getDescription(),
                        expectedDuration,
                        plan.getScheduledStart().toLocalTime(),
                        plan.getScheduledEnd().toLocalTime()
                ))
        );
    }

    public static PlanDetailResponse fromAiPlan(Plan plan, List<AiPlan> aiPlans) {
        List<PlanDetail> details = aiPlans.stream()
                .map(ai -> new PlanDetail(
                        ai.getScheduledStart().toLocalDate(),
                        ai.getDescription(),
                        ai.getExpectedDuration(),
                        ai.getScheduledStart().toLocalTime(),
                        ai.getScheduledEnd().toLocalTime()
                ))
                .toList();

        return new PlanDetailResponse(
                plan.getTitle(),
                plan.getDeadline(),
                aiPlans.get(0).getTaskRange(),
                plan.getPriority().name(),
                details
        );
    }
}
