package dgu.umc_app.domain.question.dto.request;

import dgu.umc_app.domain.question.entity.QuestionCategory;
import dgu.umc_app.domain.question.entity.UserQuestion;
import dgu.umc_app.domain.user.entity.User;
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

        public UserQuestion toEntity(User user) {
                return UserQuestion.builder()
                        .title(this.title())
                        .content(this.content())
                        .category(this.questionCategory())
                        .user(user)
                        .build();
        }
}
