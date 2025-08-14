package dgu.umc_app.domain.plan.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalTime;

public record AiPlanUpdate(

        @Schema(description = "AI 세부 일정 ID (신규 생성 시 null)")
        Long id,

        @Schema(description = "일정 수행 시작 날짜")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate date,

        @Schema(description = "할 일")
        String description,

        @Schema(description = "예상 소요 시간")
        Long expectedDuration,

        @Schema(description = "예상 시작 시간")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
        LocalTime scheduledStartTime,

        @Schema(description = "예상 종료 시간")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
        LocalTime scheduledEndTime,

        @Schema(description = "삭제 여부", example = "false")
        Boolean aiDelete
) {
}
