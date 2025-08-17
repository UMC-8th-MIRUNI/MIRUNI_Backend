package dgu.umc_app.domain.question.dto.response;

import dgu.umc_app.domain.question.dto.request.CreateUserQuestionRequestDto;
import dgu.umc_app.domain.question.entity.UserQuestion;

import java.time.LocalDateTime;

public record CreateUserQuestionResponseDto(
        String content,

        LocalDateTime createdAt

) {

    public static CreateUserQuestionResponseDto from(UserQuestion userQuestion) {
        return new CreateUserQuestionResponseDto(
            userQuestion.getContent(),
            userQuestion.getCreatedAt()
        );
    }

}
