package dgu.umc_app.domain.plan.dto;

import dgu.umc_app.domain.plan.entity.Plan;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PlanCreateResponse {

    @Schema(description = "일정 ID")
    private Long planId;

    @Schema(description = "일정 제목")
    private String title;

    @Schema(description = "일정 마감 기한")
    private LocalDateTime deadline;

    @Schema(description = "일정 완료 여부")
    private boolean isDone;

    public static PlanCreateResponse from(Plan plan) {
        return new PlanCreateResponse(
                plan.getId(),
                plan.getTitle(),
                plan.getDeadline(),
                plan.isDone()
        );
    }
}
