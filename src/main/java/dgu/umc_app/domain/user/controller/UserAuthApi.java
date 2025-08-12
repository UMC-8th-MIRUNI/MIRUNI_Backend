package dgu.umc_app.domain.user.controller;

import dgu.umc_app.domain.user.dto.request.GoogleLoginRequest;
import dgu.umc_app.domain.user.dto.request.KakaoLoginRequest;
import dgu.umc_app.domain.user.dto.request.GoogleSignUpRequest;
import dgu.umc_app.domain.user.dto.request.UserLoginRequest;
import dgu.umc_app.domain.user.dto.request.UserSignupRequest;
import dgu.umc_app.domain.user.dto.request.KakaoSignUpRequest;
import dgu.umc_app.domain.user.dto.request.ReissueTokenRequest;
import dgu.umc_app.domain.user.dto.request.SurveyRequest;
import dgu.umc_app.domain.user.dto.request.ChangePasswordRequest;
import dgu.umc_app.domain.user.dto.response.AuthLoginResponse;
import dgu.umc_app.domain.user.dto.response.ReissueTokenResponse;
import dgu.umc_app.domain.user.dto.response.SurveyResponse;
import dgu.umc_app.domain.user.dto.response.UserResponse;
import dgu.umc_app.global.authorize.LoginUser;
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
    UserResponse googleSignUp(@Valid @RequestBody GoogleSignUpRequest request, @LoginUser Long userId);

    @Operation(
        summary = "카카오 회원가입 완료",
        description = "카카오 로그인 후 추가 정보 입력으로 회원가입 완료"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "회원가입 완료",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = UserResponse.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 또는 이미 완료된 회원가입"),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    UserResponse kakaoSignUp(@Valid @RequestBody KakaoSignUpRequest request, @LoginUser Long userId);

    @Operation(
        summary = "로그아웃",
        description = "사용자 로그아웃을 처리합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
        @ApiResponse(responseCode = "401", description = "로그아웃 실패")
    })
    void logout();

    @Operation(
        summary = "토큰 재발급",
        description = "리프레시 토큰을 사용하여 새로운 액세스 토큰과 리프레시 토큰을 발급합니다. \n" +
                "Refresh Token Rotation을 적용하여 보안을 강화합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "토큰 재발급 성공",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ReissueTokenResponse.class))),
        @ApiResponse(responseCode = "401", description = "유효하지 않은 리프레시 토큰"),
        @ApiResponse(responseCode = "400", description = "입력값 검증 실패")
    })
    ReissueTokenResponse reissueToken(@Valid @RequestBody ReissueTokenRequest request);

    @Operation(
        summary = "설문조사 완료",
        description = "사용자 설문조사 응답을 저장합니다. \n" +
                "미루는 상황(다중선택), 정도(단일선택), 이유(다중선택)에 대한 응답을 enum 기반으로 검증하고 " +
                "한글 설명으로 DB에 저장하며, 사용자 상태를 ACTIVE로 변경합니다. \n" +
                "요청은 문자열(PHONE, NORMAL, PERFECTIONISM...) 형태로 가능합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "설문 완료 성공",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = SurveyResponse.class))),
        @ApiResponse(responseCode = "400", description = "입력값 검증 실패 (잘못된 enum 값 또는 범위 초과)"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
        @ApiResponse(responseCode = "409", description = "이미 설문을 완료한 사용자")
    })
    SurveyResponse survey(@Valid @RequestBody SurveyRequest request, @LoginUser Long userId);

    @Operation(
        summary = "회원 탈퇴",
        description = "현재 로그인한 사용자의 회원 탈퇴를 처리합니다. \n" +
                "소프트 삭제 방식으로 처리되며, 모든 토큰이 무효화됩니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "회원 탈퇴 성공"),
        @ApiResponse(responseCode = "400", description = "이미 탈퇴한 사용자"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    void withdrawUser(@LoginUser Long userId);

    @Operation(
        summary = "비밀번호 변경",
        description = "현재 비밀번호를 확인하고 새 비밀번호로 변경합니다. \n" +
                "비밀번호 변경 후 모든 토큰이 무효화되어 재로그인이 필요합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "비밀번호 변경 성공"),
        @ApiResponse(responseCode = "400", description = "비밀번호 형식 오류 또는 현재 비밀번호와 동일"),
        @ApiResponse(responseCode = "401", description = "현재 비밀번호 불일치"),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    void changePassword(@LoginUser Long userId, @Valid @RequestBody ChangePasswordRequest request);
} 