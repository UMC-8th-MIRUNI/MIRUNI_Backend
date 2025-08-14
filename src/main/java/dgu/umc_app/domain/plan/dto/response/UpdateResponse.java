package dgu.umc_app.domain.plan.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "일정 수정 결과")
public record UpdateResponse(
        @Schema(description = "상위 Plan ID", example = "12")
        Long planId,

        @Schema(description = "갱신 시각", example = "2025-08-14T06:22:10.123")
        LocalDateTime updatedAt
) {
    public static UpdateResponse fromPlan(Long planId, LocalDateTime updatedAt) {
        return new UpdateResponse(planId, updatedAt);
    }

}
