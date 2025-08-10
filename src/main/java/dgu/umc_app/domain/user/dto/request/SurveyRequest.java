package dgu.umc_app.domain.user.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "설문조사 요청 - 번호로 응답")
public record SurveyRequest(

    @Schema(description = "첫 번째 질문: 어떤 상황에서 미루게 되나요? (1-5번 중 복수선택)", 
            example = "[1, 3, 4]")
    @NotNull(message = "첫 번째 질문 응답은 필수입니다.")
    @Size(min = 1, max = 5, message = "1-5개 항목을 선택해야 합니다.")
    List<@Min(1) @Max(5) Integer> delaySituation,

    @Schema(description = "두 번째 질문: 평소 얼마나 미루는 편인가요? (1-5점)", 
            example = "3")
    @NotNull(message = "두 번째 질문 응답은 필수입니다.")
    @Min(value = 1, message = "1 이상이어야 합니다")
    @Max(value = 5, message = "5 이하여야 합니다")
    Integer delayDegree,

    @Schema(description = "세 번째 질문: 미루는 주된 이유는 무엇인가요? (1-6번 중 복수선택)", 
            example = "[2, 5]")
    @NotNull(message = "세 번째 질문 응답은 필수입니다.")
    @Size(min = 1, max = 6, message = "1-6개 항목을 선택해야 합니다.")
    List<@Min(1) @Max(6) Integer> delayReason
        
) {}