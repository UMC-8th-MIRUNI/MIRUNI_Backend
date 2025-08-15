package dgu.umc_app.domain.plan.dto.response;

import dgu.umc_app.domain.plan.entity.AiPlan;
import dgu.umc_app.domain.plan.entity.Plan;
import dgu.umc_app.domain.plan.entity.Status;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record PlanFinishResponse(

        @Schema(description = "일정 아이디")
        Long planId,

        @Schema(description = "일정 상태")
        Status status,

        @Schema(description = "땅콩 개수")
        int peanutCount,

        @Schema(description = "일정 수행 시작 날짜")
        LocalDateTime scheduledStart,

        @Schema(description = "일정 수행 종료 날짜")
        LocalDateTime scheduledEnd
) {
    public static PlanFinishResponse from(Plan plan, int peanutCount) {
        return new PlanFinishResponse(
                plan.getId(),
                plan.getStatus(),
                peanutCount,
                plan.getScheduledStart(),
                plan.getScheduledEnd()
        );
    }

    public static PlanFinishResponse from(AiPlan aiplan, int peanutCount) {
        return new PlanFinishResponse(
                aiplan.getId(),
                aiplan.getStatus(),
                peanutCount,
                aiplan.getScheduledStart(),
                aiplan.getScheduledEnd()
        );
    }
}
