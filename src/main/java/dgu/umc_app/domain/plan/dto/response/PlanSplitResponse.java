package dgu.umc_app.domain.plan.dto.response;

import dgu.umc_app.domain.plan.entity.AiPlan;
import dgu.umc_app.domain.plan.entity.Plan;
import dgu.umc_app.domain.plan.entity.PlanType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record   PlanSplitResponse(

        @Schema(description = "실행 순서")
        Long stepOrder,

        @Schema(description = "작업 유형")
        PlanType planType,

        @Schema(description = "일정 범위")
        String taskRange,

        @Schema(description = "일정 내용")
        String description,

        @Schema(description = "예상 시간")
        Long expectedDuration,

        @Schema(description = "실행 날짜")
        LocalDate scheduledDate,

        @Schema(description = "시작 시간")
        LocalTime startTime,

        @Schema(description = "종료 시간")
        LocalTime endTime
) {
    public static PlanSplitResponse from(AiPlan aiPlan) {
        return new PlanSplitResponse(
                aiPlan.getStepOrder(),
                aiPlan.getPlanType(),
                aiPlan.getTaskRange(),
                aiPlan.getDescription(),
                aiPlan.getExpectedDuration(),
                aiPlan.getScheduledDate(),
                aiPlan.getStartTime(),
                aiPlan.getEndTime()
        );
    }

    public static List<PlanSplitResponse> fromList(List<AiPlan> aiPlans) {
        return aiPlans.stream()
                .map(PlanSplitResponse::from)
                .toList();
    }

    public static List<AiPlan> toEntities(
            List<PlanSplitResponse> responses,
            Plan parentPlan,
            PlanType planType,
            String taskRange
    ) {
        return responses.stream()
                .map(response -> AiPlan.builder()
                        .plan(parentPlan)
                        .stepOrder(response.stepOrder())
                        .planType(planType)
                        .taskRange(taskRange)
                        .description(response.description())
                        .expectedDuration(response.expectedDuration())
                        .scheduledDate(response.scheduledDate())
                        .startTime(response.startTime())
                        .endTime(response.endTime())
                        .priority(parentPlan.getPriority())
                        .build()
                ).toList();
    }
}
