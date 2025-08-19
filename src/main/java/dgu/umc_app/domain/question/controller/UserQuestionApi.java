package dgu.umc_app.domain.question.controller;

import dgu.umc_app.domain.question.dto.request.CreateUserQuestionRequestDto;
import dgu.umc_app.domain.question.dto.response.CreateUserQuestionResponseDto;
import dgu.umc_app.global.authorize.LoginUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "UserQuestion", description = "고객센터 문의 API")
public interface UserQuestionApi {



    @Operation(
            summary = "문의 남기기 API",
            description = "문의 남기기 API 입니다."
    )
    @ApiResponse(responseCode = "200", description = "문의 남기기 성공",
            content = @Content (mediaType = "application/json",
            schema = @Schema(implementation = dgu.umc_app.global.response.ApiResponse.class),
            examples = @ExampleObject(
                    name = "성공 응답",
                    value = """
            {
                "errorCode" : null,
                "message" : "OK",
                "result" : {
                    "content": "시간표 기능은 어떻게 수정하나요?",
                    "createdAt": "2025-08-16T14:00:00"
                }
            }
            """
            )

    ))
    public CreateUserQuestionResponseDto createQuestion(@LoginUser Long userId,
            @Valid @RequestBody CreateUserQuestionRequestDto request);
}
