package dgu.umc_app.domain.plan.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import dgu.umc_app.domain.plan.entity.Category;
import dgu.umc_app.domain.plan.entity.Priority;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public record AiPlanUpdateRequest(

        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "AI")
        @NotNull
        Category category,

        @Schema(description = "상위 일정 제목")
        String title,

        @Schema(description = "마감기한", example = "2025-08-30T23:59:59")
        LocalDateTime deadline,

        @Schema(description = "일정 범위")
        String taskRange,

        @Schema(description = "우선 순위")
        Priority priority,

        @Schema(description = "세부 일정 리스트")
        List<AiDetailUpdate> plans
) implements ScheduleUpdateRequest{ }
