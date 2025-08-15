package dgu.umc_app.domain.plan.dto.request;

import dgu.umc_app.domain.plan.entity.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record PlanDeleteRequest(
        @NotNull
        @Schema(description = "BASIC | AI")
        Category category,

        @NotNull
        @Schema(description = "일반 일정 아이디")
        Long planId,    // 둘 다 필요 (AI도 소속 검증용)

        @Schema(description = "세부 일정")
        List<Long> aiPlanIds  // BASIC이면 null/empty, AI이면 필수(1개 이상)
) {
}
