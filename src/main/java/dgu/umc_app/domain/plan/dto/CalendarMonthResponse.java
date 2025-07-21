package dgu.umc_app.domain.plan.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class CalendarMonthResponse {

    @Schema(description = "일정 마감기한(yyyy-mm-dd")
    private LocalDate deadline;

    @Schema(description = "일정 목록")
    private List<ScheduleInfo> schedules;

    @Getter
    @AllArgsConstructor
    public static class ScheduleInfo {
        @Schema(description = "일정 ID")
        private Long scheduleId;

        @Schema(description = "일정 제목")
        private String title;

        @Schema(description = "완료 여부")
        private boolean isDone;

        @Schema(description = "일정 유형 (NORMAL / AI_SPLIT)")
        private String type;
    }
}

