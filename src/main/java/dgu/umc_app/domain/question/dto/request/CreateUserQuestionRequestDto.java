package dgu.umc_app.domain.question.dto.request;

import dgu.umc_app.domain.question.entity.QuestionCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record CreateUserQuestionRequestDto(

        @Length(min = 2, max = 20)
        @Schema(example = "앱 오류")
        String title,

        @NotBlank
        @Schema(example = "작동이 안돼요")
        String content,

        @NotNull
        @Schema(example = "ACCOUNT",
                allowableValues = {"ACCOUNT", "PLANNING", "ALARM", "FOCUS_MODE", "ETC"} )
        QuestionCategory questionCategory
) {
}
