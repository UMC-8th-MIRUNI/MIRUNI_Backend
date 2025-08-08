package dgu.umc_app.domain.plan.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalTime;

public record PlanDetail(

        @Schema(description = "일정 수행 시작 날짜")
        LocalDate date,

        @Schema(description = "할 일")
        String description,

        @Schema(description = "예상 소요 시간")
        Long expectedDuration,

        @Schema(description = "예상 시작 시간")
        LocalTime scheduledStartTime,

        @Schema(description = "예상 종료 시간")
        LocalTime scheduledEndTime
) {
}
