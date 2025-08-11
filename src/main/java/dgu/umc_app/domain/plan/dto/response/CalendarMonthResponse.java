package dgu.umc_app.domain.plan.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;


public record CalendarMonthResponse (

        @Schema(description = "해당날짜")
        LocalDate date,

        @Schema(description = "하지 않은 총 일정 갯수")
        int scheduleCount
) {
    public LocalDate getDate() {
        return this.date;
    }
}

