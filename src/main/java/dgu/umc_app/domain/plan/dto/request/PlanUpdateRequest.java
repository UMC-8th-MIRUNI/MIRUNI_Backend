package dgu.umc_app.domain.plan.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import dgu.umc_app.domain.plan.entity.*;
import dgu.umc_app.domain.plan.exception.AiPlanErrorCode;
import dgu.umc_app.domain.plan.exception.PlanErrorCode;
import dgu.umc_app.global.exception.BaseException;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public record PlanUpdateRequest(

        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "BASIC")
        @NotNull
        Category category,

        @Schema(description = "일정 제목")
        String title,

        @Schema(description = "마감기한", example = "2025-08-30T23:59:59")
        LocalDateTime deadline,

        @Schema(description = "우선 순위")
        Priority priority,

        @Schema(description = "실행시작날짜")
        LocalDateTime scheduledStart,

        @Schema(description = "실행종료날짜")
        LocalDateTime scheduledEnd,

        @Schema(description = "일정 간단 설명")
        String description,

        @Schema(description = "삭제 여부")
        Boolean delete
) implements ScheduleUpdateRequest { }


