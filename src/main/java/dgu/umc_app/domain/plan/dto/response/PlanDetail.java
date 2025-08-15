package dgu.umc_app.domain.plan.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalTime;

public record PlanDetail(

        @Schema(description = "일정 ID", example = "1")
        Long planId,

        @Schema(description = "일정 수행 날짜", example = "2025-08-20")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate date,

        @Schema(description = "세부 일정 내용", example = "와이어 프레임 만들기")
        String description,

        @Schema(description = "예상 소요 시간", example = "120")
        Long expectedDuration,

        @Schema(description = "예상 시작 시간", example = "15:00:00")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
        LocalTime startTime,

        @Schema(description = "예상 종료 시간", example = "16:30:00")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
        LocalTime endTime
) {
}
