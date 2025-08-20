package dgu.umc_app.domain.question.dto.request;

import dgu.umc_app.domain.question.entity.UserQuestion;
import dgu.umc_app.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record CreateUserQuestionRequestDto(

        @NotBlank
        @Schema(example = "시간표 기능은 어떻게 수정하나요?")
        String content,

        @NotBlank
        @Schema(example = "010-1234-5678")
        String phoneNumber,

        @NotNull
        @Schema(example = "true")
        Boolean agreeToPersonalInfo

) {

        public UserQuestion toEntity(User user) {
                return UserQuestion.builder()
                        .content(this.content())
                        .phoneNumber(this.phoneNumber())
                        .agreeToPersonalInfo(this.agreeToPersonalInfo())
                        .user(user)
                        .build();
        }
}
