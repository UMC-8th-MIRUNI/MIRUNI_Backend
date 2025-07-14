package dgu.umc_app.domain.question.service;


import dgu.umc_app.domain.question.dto.request.CreateUserQuestionRequestDto;
import dgu.umc_app.domain.question.dto.response.CreateUserQuestionResponseDto;
import dgu.umc_app.domain.question.entity.UserQuestion;
import dgu.umc_app.domain.question.repository.UserQuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserQuestionCommandService {

    private final UserQuestionRepository userQuestionRepository;

    @Transactional
    public CreateUserQuestionResponseDto createQuestion(CreateUserQuestionRequestDto request) {

        UserQuestion userQuestion = request.toEntity();

        userQuestionRepository.save(userQuestion);

        return CreateUserQuestionResponseDto.from(userQuestion);

    }
}
