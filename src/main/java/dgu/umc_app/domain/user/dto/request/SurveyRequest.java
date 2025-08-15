package dgu.umc_app.domain.user.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Set;
import dgu.umc_app.domain.user.entity.DelaySituation;
import dgu.umc_app.domain.user.entity.DelayLevel;
import dgu.umc_app.domain.user.entity.DelayReason;

@Schema(description = "설문조사 요청 - enum 타입으로 응답")
public record SurveyRequest(

        @Schema(description = "첫 번째 질문: 어떤 상황에서 미루게 되나요? (복수선택)",
                example = "[\"PHONE\", \"TOO_TIRED\"]")
        @NotEmpty(message = "첫 번째 질문 응답은 필수입니다.")
        @Size(min = 1, max = 5, message = "1-5개 항목을 선택해야 합니다.")
        Set<DelaySituation> situations,

        @Schema(description = "두 번째 질문: 평소 얼마나 미루는 편인가요?",
                example = "NORMAL")
        @NotNull(message = "두 번째 질문 응답은 필수입니다.")
        DelayLevel level,

        @Schema(description = "세 번째 질문: 미루는 주된 이유는 무엇인가요? (복수선택)",
                example = "[\"PERFECTIONISM\", \"NOT_FUN\"]")
        @NotEmpty(message = "세 번째 질문 응답은 필수입니다.")
        @Size(min = 1, max = 6, message = "1-6개 항목을 선택해야 합니다.")
        Set<DelayReason> reasons

) {}