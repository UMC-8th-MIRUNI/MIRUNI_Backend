package dgu.umc_app.domain.question.controller;

import dgu.umc_app.domain.question.dto.request.CreateUserQuestionRequestDto;
import dgu.umc_app.domain.question.dto.response.CreateUserQuestionResponseDto;
import dgu.umc_app.domain.question.service.UserQuestionCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/support")
public class UserQuestionController implements UserQuestionApi {

    private final UserQuestionCommandService userQuestionCommandService;


    @Override
    @PostMapping
    public CreateUserQuestionResponseDto createQuestion(CreateUserQuestionRequestDto request) {
        return userQuestionCommandService.createQuestion(request);
    }
}
