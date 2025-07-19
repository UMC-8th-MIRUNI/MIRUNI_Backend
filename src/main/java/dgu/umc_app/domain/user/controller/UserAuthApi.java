package dgu.umc_app.domain.user.controller;

import dgu.umc_app.domain.user.dto.request.UserLoginRequest;
import dgu.umc_app.domain.user.dto.request.UserSignupRequest;
import dgu.umc_app.domain.user.dto.response.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "User", description = "회원가입, 로그인 API")
public interface UserAuthApi {

    @Operation(
            summary = "회원가입",
            description = "새로운 사용자를 생성합니다. \n" +
                    "이메일 중복 체크 후 비밀번호 암호화하여 저장합니다. \n" +
                    "회원가입 성공 시 사용자 정보를 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "회원가입 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "409", description = "이미 이메일이 존재함"),
            @ApiResponse(responseCode = "400", description = "입력값 검증 실패")
    })
    UserResponse signup(@RequestBody UserSignupRequest request);

    @Operation(
            summary = "로그인",
            description = "사용자 로그인을 처리합니다. \n" +
                    "이메일과 비밀번호 검증 후 JWT 토큰을 발급합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
            @ApiResponse(responseCode = "401", description = "비밀번호가 틀림"),
            @ApiResponse(responseCode = "400", description = "입력값 검증 실패")
    })
    UserResponse login(@RequestBody UserLoginRequest request);
} 