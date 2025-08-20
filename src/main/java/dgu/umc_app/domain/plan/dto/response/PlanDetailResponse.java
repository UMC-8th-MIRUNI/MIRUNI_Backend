package dgu.umc_app.domain.plan.dto.response;

import dgu.umc_app.domain.plan.entity.AiPlan;
import dgu.umc_app.domain.plan.entity.Category;
import dgu.umc_app.domain.plan.entity.Plan;
import dgu.umc_app.domain.plan.entity.Priority;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.time.Duration;
import java.util.List;

import static dgu.umc_app.global.common.DateTimeFormatUtil.*;

public record PlanDetailResponse(

        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "BASIC")
        @NotNull
        Category category,

        @Schema(description = "일정 제목", example = "부모님 생신 준비")
        String title,

        @Schema(description = "마감기한", example = "2025-08-30T23:59:59")
        String deadline,

        @Schema(description = "우선 순위", example = "HIGH")
        Priority priority,

        @Schema(description = "실행시작날짜", example = "2025-08-17T11:00:00")
        LocalDateTime scheduledStart,

        @Schema(description = "실행종료날짜", example = "2025-08-18T10:00:00")
        LocalDateTime scheduledEnd,

        @Schema(description = "일정 한줄소개", example = "축하 영상 제작")
        String description
) implements ScheduleDetailResponse{
    public static PlanDetailResponse fromPlan(Plan plan) {
        return new PlanDetailResponse(
                Category.BASIC,
                plan.getTitle(),
                plan.getDeadline().format(DATE_TIME_FORMATTER),
                plan.getPriority(),
                plan.getScheduledStart(),
                plan.getScheduledEnd(),
                plan.getDescription()
        );
    }
}
