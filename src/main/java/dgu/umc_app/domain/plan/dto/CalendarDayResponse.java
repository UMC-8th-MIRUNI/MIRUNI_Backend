package dgu.umc_app.domain.plan.dto;

import dgu.umc_app.domain.plan.entity.AiPlan;
import dgu.umc_app.domain.plan.entity.Priority;
import dgu.umc_app.domain.plan.entity.Plan;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalTime;

public record CalendarDayResponse (

        @Schema(description = "일정 ID")
        Long id,

        @Schema(description = "AI 쪼개기 일정일 경우 상위일정 제목")
        String parentTitle,

        @Schema(description = "일반 일정일 경우 상위일정 제목")
        String title,

        @Schema(description = "각 일정의 시작 시간")
        LocalTime startTime,

        @Schema(description = "각 일정의 종료 시간")
        LocalTime endTime,

        @Schema(description = "각 일정의 우선순위")
        Priority priority,

        @Schema(description = "일정 유형(개수 세기 위함)")
        String category
){

    public static CalendarDayResponse from(Plan plan) {
        return new CalendarDayResponse(
                plan.getId(),
                null,
                plan.getTitle(),
                plan.getExecuteDate().toLocalTime(),
                plan.getDeadline().toLocalTime(),
                plan.getPriority(),
                "BASIC"
        );
    }

    public static CalendarDayResponse from(AiPlan aiPlan) {
        return new CalendarDayResponse(
                aiPlan.getId(),
                aiPlan.getPlan().getTitle(),
                aiPlan.getDescription(),
                aiPlan.getStartTime(),
                aiPlan.getEndTime(),
                aiPlan.getPriority(),
                "AI"
        );
    }
}
