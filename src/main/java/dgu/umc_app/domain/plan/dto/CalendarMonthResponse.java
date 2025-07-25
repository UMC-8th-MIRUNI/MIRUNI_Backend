package dgu.umc_app.domain.plan.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

public record CalendarMonthResponse (

    @Schema(description = "일정 마감기한(yyyy-mm-dd")
    LocalDate deadline,

    @Schema(description = "일정 목록")
    List<ScheduleInfo> schedules
) {
    public static record ScheduleInfo(
            @Schema(description = "일정 ID")
            Long scheduleId,

            @Schema(description = "일정 제목")
            String title,

            @Schema(description = "완료 여부")
            boolean isDone,

            @Schema(description = "일정 유형 (NORMAL / AI_SPLIT)")
            String type
    ) { }
}

