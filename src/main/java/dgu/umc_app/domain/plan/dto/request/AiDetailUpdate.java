package dgu.umc_app.domain.plan.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalTime;

public record AiDetailUpdate(
        @Schema(description = "세부 일정 ID")
        Long aiPlanId,

        @Schema(description = "세부 일정 수행 날짜", example = "2025-08-22")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate date,

        @Schema(description = "세부 일정 내용", example = "기획안 작성 4-5페이지까지")
        String description,

        @Schema(description = "예상 소요 시간", example = "120")
        Long expectedDuration,

        @Schema(description = "예상 시작 시간", example = "15:00:00")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
        LocalTime startTime,

        @Schema(description = "예상 종료 시간", example = "16:30:00")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
        LocalTime endTime,

        @Schema(description = "삭제 여부")
        Boolean delete
) {
}
