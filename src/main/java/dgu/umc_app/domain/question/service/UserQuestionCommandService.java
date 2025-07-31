package dgu.umc_app.domain.question.service;

import dgu.umc_app.domain.question.dto.request.CreateUserQuestionRequestDto;
import dgu.umc_app.domain.question.dto.response.CreateUserQuestionResponseDto;
import dgu.umc_app.domain.question.entity.UserQuestion;
import dgu.umc_app.domain.question.repository.UserQuestionRepository;
import dgu.umc_app.domain.user.entity.User;
import dgu.umc_app.domain.user.exception.UserErrorCode;
import dgu.umc_app.domain.user.repository.UserRepository;
import dgu.umc_app.global.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserQuestionCommandService {

    private final UserQuestionRepository userQuestionRepository;
    private final UserRepository userRepository;

    @Transactional
    public CreateUserQuestionResponseDto createQuestion(Long userId, CreateUserQuestionRequestDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> BaseException.type(UserErrorCode.USER_NOT_FOUND));

        UserQuestion userQuestion = request.toEntity(user);

        userQuestionRepository.save(userQuestion);

        return CreateUserQuestionResponseDto.from(userQuestion);

    }
}
