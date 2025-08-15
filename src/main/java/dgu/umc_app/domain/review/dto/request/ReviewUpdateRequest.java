package dgu.umc_app.domain.review.dto.request;

import dgu.umc_app.domain.review.entity.Mood;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

public record ReviewUpdateRequest(

        @NotNull(message = "기분은 필수입니다.")
        @Schema(example = "ANGRY")
        Mood mood,

        @Min(value = 0, message = "성취도는 0 이상이어야 합니다.")
        @Max(value = 100, message = "성취도는 100 이하여야 합니다.")
        @Schema(example = "95")
        int achievement,

        @NotBlank(message = "회고 메모는 비워둘 수 없습니다.")
        @Size(max = 255, message = "회고 메모는 255자 이내여야 합니다.")
        @Schema(example = "수정된 회고 메모입니다. 목표보다 훨씬 잘 완료했고, 추가적인 개선사항도 발견했습니다.")
        String memo
) {
}