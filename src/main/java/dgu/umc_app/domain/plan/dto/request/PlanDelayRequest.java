package dgu.umc_app.domain.plan.dto.request;

import dgu.umc_app.domain.plan.entity.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDateTime;

public record PlanDelayRequest(
        @Schema(description = "미룰 날짜와 시간")
        @NotNull
        LocalDateTime newStartDateTime,

        @Schema(description = "예상 소요 시간")
        @NotNull
        @PositiveOrZero
        Integer expectedMinutes,

        @Schema(description = "일정 유형(미루기 일정 판단 위함, BASIC or AI)")
        @NotNull
        Category category,

        @Schema(description = "수행 시간")
        @NotNull
        Long executeTime
) {}

