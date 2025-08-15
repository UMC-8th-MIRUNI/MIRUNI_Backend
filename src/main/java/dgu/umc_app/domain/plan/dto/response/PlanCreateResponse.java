package dgu.umc_app.domain.plan.dto.response;

import dgu.umc_app.domain.plan.entity.Plan;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record PlanCreateResponse(

        @Schema(description = "일정 ID")
        Long planId,

        @Schema(description = "일정 제목")
        String title,

        @Schema(description = "일정 마감 기한")
        LocalDateTime deadline,

        @Schema(description = "일정 수행 시작날짜")
        LocalDateTime scheduledStart,

        @Schema(description = "일정 수행 종료날짜")
        LocalDateTime scheduledEnd,

        @Schema(description = "일정 완료 여부")
        boolean isDone

) {
    public static PlanCreateResponse from(Plan plan) {
        return new PlanCreateResponse(
                plan.getId(),
                plan.getTitle(),
                plan.getDeadline(),
                plan.getScheduledStart(),
                plan.getScheduledEnd(),
                plan.isDone()
        );
    }
}
