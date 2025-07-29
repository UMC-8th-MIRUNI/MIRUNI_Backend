package dgu.umc_app.domain.user.controller;

import dgu.umc_app.domain.user.dto.request.GoogleLoginRequest;
import dgu.umc_app.domain.user.dto.request.KakaoLoginRequest;
import dgu.umc_app.domain.user.dto.request.GoogleSignUpRequest;
import dgu.umc_app.domain.user.dto.request.UserLoginRequest;
import dgu.umc_app.domain.user.dto.request.UserSignupRequest;
import dgu.umc_app.domain.user.dto.response.AuthLoginResponse;
import dgu.umc_app.domain.user.dto.response.UserResponse;
import dgu.umc_app.domain.user.entity.User;
import dgu.umc_app.global.annotation.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

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
    UserResponse signup(@Valid @RequestBody UserSignupRequest request);

    @Operation(
        summary = "이메일 중복 확인",
        description = "회원가입 시 이메일 중복 여부를 확인합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "사용 가능한 이메일"),
        @ApiResponse(responseCode = "409", description = "이미 존재하는 이메일"),
        @ApiResponse(responseCode = "400", description = "입력값 검증 실패")
    })
    void duplicateCheck(@RequestParam @Email String email);
    
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
    UserResponse login(@Valid @RequestBody UserLoginRequest request);

    @Operation(
        summary = "구글 로그인",
        description = "구글 ID 토큰을 받아 로그인/회원가입 및 JWT 발급"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "로그인 성공",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = AuthLoginResponse.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 또는 인증 실패")
    })
    AuthLoginResponse googleLogin(@Valid @RequestBody GoogleLoginRequest request);

    @Operation(
        summary = "카카오 로그인",
        description = "카카오 Access Token을 받아 로그인/회원가입 및 JWT 발급"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "로그인 성공",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = AuthLoginResponse.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 또는 인증 실패")
    })
    AuthLoginResponse kakaoLogin(@Valid @RequestBody KakaoLoginRequest request);

    @Operation(
        summary = "구글 회원가입 완료",
        description = "구글 로그인 후 추가 정보 입력으로 회원가입 완료"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "회원가입 완료",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = UserResponse.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 또는 이미 완료된 회원가입"),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    UserResponse googleSignUp(@Valid @RequestBody GoogleSignUpRequest request, @CurrentUser User user);
} 