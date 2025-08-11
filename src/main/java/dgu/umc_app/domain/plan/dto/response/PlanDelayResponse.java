package dgu.umc_app.domain.plan.dto.response;

import dgu.umc_app.domain.plan.entity.AiPlan;
import dgu.umc_app.domain.plan.entity.Plan;
import dgu.umc_app.domain.plan.entity.Status;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record PlanDelayResponse(

        @Schema(description = "일정 아이디")
        Long planId,

        @Schema(description = "일정 상태")
        Status status,

        @Schema(description = "예정 수행시작 날짜시각")
        LocalDateTime scheduledStart,

        @Schema(description = "예정 수행종료 날짜시각")
        LocalDateTime scheduledEnd,

        @Schema(description = "미룬 시간(분)")
        long delayTime,

        @Schema(description = "수행 시간(분)")
        long executeTime,

        @Schema(description = "중지한 시각")
        LocalDateTime stoppedAt
) {
    public static PlanDelayResponse from(Plan plan, long delayTime, long executeTime, LocalDateTime stoppedAt) {
        return new PlanDelayResponse(
                plan.getId(),
                plan.getStatus(),
                plan.getScheduledStart(),
                plan.getScheduledEnd(),
                delayTime,
                executeTime,
                stoppedAt
        );
    }

    public static PlanDelayResponse from(AiPlan aiPlan, long delayTime, long executeTime, LocalDateTime stoppedAt) {
        return new PlanDelayResponse(
                aiPlan.getId(),
                aiPlan.getStatus(),
                aiPlan.getScheduledStart(),
                aiPlan.getScheduledEnd(),
                delayTime,
                executeTime,
                stoppedAt
        );
    }
}
