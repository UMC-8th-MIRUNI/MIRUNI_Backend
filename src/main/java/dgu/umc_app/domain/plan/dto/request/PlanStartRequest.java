package dgu.umc_app.domain.plan.dto.request;

import dgu.umc_app.domain.plan.entity.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record PlanStartRequest(
        @Schema(description = "일정 카테고리(BASIC or AI)")
        @NotNull
        Category category
) {
}
