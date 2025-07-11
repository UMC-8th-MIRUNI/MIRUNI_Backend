package dgu.umc_app.domain.user.controller;

import dgu.umc_app.domain.user.dto.request.UserLoginRequest;
import dgu.umc_app.domain.user.dto.request.UserSignupRequest;
import dgu.umc_app.domain.user.dto.response.UserLoginResponse;
import dgu.umc_app.domain.user.dto.response.UserSignupResponse;
import dgu.umc_app.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "User", description = "회원가입, 로그인 API")
public interface UserApi {

    @Operation(
            summary = "회원가입",
            description = "새로운 사용자를 생성합니다. \n" +
                    "이메일 중복 체크 후 비밀번호 암호화하여 저장합니다. \n" +
                    "회원가입 성공 시 사용자 정보를 반환합니다."
    )
    ResponseEntity<ApiResponse<UserSignupResponse>> signup(@RequestBody UserSignupRequest request);

    @Operation(
            summary = "로그인",
            description = "사용자 로그인을 처리합니다. \n" +
                    "이메일과 비밀번호 검증 후 JWT 토큰을 발급합니다."
    )
    ResponseEntity<ApiResponse<UserLoginResponse>> login(@RequestBody UserLoginRequest request);
} 