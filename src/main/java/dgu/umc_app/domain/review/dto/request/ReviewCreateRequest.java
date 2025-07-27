package dgu.umc_app.domain.review.dto.request;

import dgu.umc_app.domain.review.entity.Mood;
import jakarta.validation.constraints.*;

public record ReviewCreateRequest(

        @NotNull(message = "aiPlanId는 필수입니다.")
        Long aiPlanId,

        @NotNull(message = "planId는 필수입니다.")
        Long planId,

        @NotNull(message = "기분은 필수입니다.")
        Mood mood,

        @Min(value = 0, message = "성취도는 0 이상이어야 합니다.")
        @Max(value = 100, message = "성취도는 100 이하여야 합니다.")
        int achievement,

        @NotBlank(message = "회고 메모는 비워둘 수 없습니다.")
        @Size(max = 255, message = "회고 메모는 255자 이내여야 합니다.")
        String memo

) {
}
