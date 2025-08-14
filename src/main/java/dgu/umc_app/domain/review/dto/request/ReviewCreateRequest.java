package dgu.umc_app.domain.review.dto.request;

import dgu.umc_app.domain.review.entity.Mood;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

public record ReviewCreateRequest(

        @Schema(description = "AI 계획 ID (일반 Plan 회고인 경우 null)", example = "5")
        Long aiPlanId,

        @NotNull(message = "planId는 필수입니다.")
        @Schema(example = "1")
        Long planId,

        @NotNull(message = "기분은 필수입니다.")
        @Schema(example = "ANGRY")
        Mood mood,

        @Min(value = 0, message = "성취도는 0 이상이어야 합니다.")
        @Max(value = 100, message = "성취도는 100 이하여야 합니다.")
        @Schema(example = "85")
        int achievement,

        @NotBlank(message = "회고 메모는 비워둘 수 없습니다.")
        @Size(max = 255, message = "회고 메모는 255자 이내여야 합니다.")
        @Schema(example = "예상보다 빨리 완료했고, 팀원들의 피드백도 좋았다.")
        String memo

) {
}
