package dgu.umc_app.domain.plan.dto.response;

import dgu.umc_app.domain.plan.entity.AiPlan;
import dgu.umc_app.domain.plan.entity.Plan;
import dgu.umc_app.domain.plan.entity.PlanType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public record   PlanSplitResponse(

        @Schema(description = "실행 순서")
        Long stepOrder,

        @Schema(description = "실행 날짜")
        LocalDate scheduledDate,

        @Schema(description = "일정 내용")
        String description,

        @Schema(description = "예상 소요 시간(분)")
        Long expectedDuration,

        @Schema(description = "시작 시간")
        LocalTime startTime,

        @Schema(description = "종료 시간")
        LocalTime endTime
) {
    public static PlanSplitResponse from(AiPlan aiPlan) {
        return new PlanSplitResponse(
                aiPlan.getStepOrder(),
                aiPlan.getScheduledStart().toLocalDate(),
                aiPlan.getDescription(),
                aiPlan.getExpectedDuration(),
                aiPlan.getScheduledStart().toLocalTime(),
                aiPlan.getScheduledEnd().toLocalTime()
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
                .map(response -> {
                            LocalDateTime scheduledStart = response.scheduledDate().atTime(response.startTime());
                            LocalDateTime scheduledEnd = response.scheduledDate().atTime(response.endTime());

                            return AiPlan.builder()
                                    .plan(parentPlan)
                                    .planType(planType)
                                    .taskRange(taskRange)
                                    .stepOrder(response.stepOrder())
                                    .scheduledStart(scheduledStart)
                                    .scheduledEnd(scheduledEnd)
                                    .description(response.description())
                                    .expectedDuration(response.expectedDuration())
                                    .priority(parentPlan.getPriority())
                                    .build();
                        }).toList();
    }
}
