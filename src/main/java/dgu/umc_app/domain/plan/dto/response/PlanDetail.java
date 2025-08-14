package dgu.umc_app.domain.plan.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalTime;

public record PlanDetail(

        @Schema(description = "일정 ID")
        Long planId,

        @Schema(description = "일정 수행 날짜")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate date,

        @Schema(description = "일반 세부 일정")
        String description,

        @Schema(description = "예상 시작 시간")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
        LocalTime startTime,

        @Schema(description = "예상 종료 시간")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
        LocalTime endTime
) {
}
