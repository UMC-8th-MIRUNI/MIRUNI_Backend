package dgu.umc_app.domain.plan.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record CalendarDayWrapperResponse(
        @Schema(description = "총 일정 개수")
        int totalCount,

        @Schema(description = "해당 날짜의 일정 리스트")
        List<CalendarDayResponse> schedules
) { }
