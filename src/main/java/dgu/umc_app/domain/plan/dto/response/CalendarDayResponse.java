package dgu.umc_app.domain.plan.dto.response;

import dgu.umc_app.domain.plan.entity.*;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalTime;

public record CalendarDayResponse (

        @Schema(description = "일정 ID")
        Long id,

        @Schema(description = "각 일정의 시작 시간")
        LocalTime startTime,

        @Schema(description = "각 일정의 종료 시간")
        LocalTime endTime,

        @Schema(description = "AI 일정일 경우 상위일정 제목")
        String parentTitle,

        @Schema(description = "AI 일정일 경우 세부일정 제목")
        String title,

        @Schema(description = "각 일정의 우선순위")
        Priority priority,

        @Schema(description = "일정 유형(미루기 일정 판단)")
        Category category
){

    public static CalendarDayResponse from(Plan plan) {
        return new CalendarDayResponse(
                plan.getId(),
                plan.getScheduledStart().toLocalTime(),
                plan.getScheduledEnd().toLocalTime(),
                plan.getTitle(),
                null,
                plan.getPriority(),
                Category.BASIC
        );
    }

    public static CalendarDayResponse from(AiPlan aiPlan) {
        return new CalendarDayResponse(
                aiPlan.getId(),
                aiPlan.getScheduledStart().toLocalTime(),
                aiPlan.getScheduledEnd().toLocalTime(),
                aiPlan.getPlan().getTitle(),
                aiPlan.getDescription(),
                aiPlan.getPriority(),
                Category.AI
        );
    }
}
