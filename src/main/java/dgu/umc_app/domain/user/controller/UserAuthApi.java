package dgu.umc_app.domain.user.controller;

import dgu.umc_app.domain.user.dto.request.GoogleLoginRequest;
import dgu.umc_app.domain.user.dto.request.KakaoLoginRequest;
import dgu.umc_app.domain.user.dto.request.GoogleSignUpRequest;
import dgu.umc_app.domain.user.dto.request.UserLoginRequest;
import dgu.umc_app.domain.user.dto.request.UserSignupRequest;
import dgu.umc_app.domain.user.dto.request.VerifyResetCodeRequest;
import dgu.umc_app.domain.user.dto.request.KakaoSignUpRequest;
import dgu.umc_app.domain.user.dto.request.ReissueTokenRequest;
import dgu.umc_app.domain.user.dto.request.PasswordResetRequest;
import dgu.umc_app.domain.user.dto.request.ResetPasswordRequest;
import dgu.umc_app.domain.user.dto.request.SurveyRequest;
import dgu.umc_app.domain.user.dto.request.ChangePasswordRequest;
import dgu.umc_app.domain.user.dto.response.AuthLoginResponse;
import dgu.umc_app.domain.user.dto.response.ReissueTokenResponse;
import dgu.umc_app.domain.user.dto.response.SurveyResponse;
import dgu.umc_app.domain.user.dto.response.UserResponse;
import dgu.umc_app.domain.user.dto.response.VerifyResponse;
import dgu.umc_app.global.authorize.LoginUser;
import dgu.umc_app.global.exception.CustomErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestHeader;

@Tag(name = "UserAuth", description = "회원가입, 로그인 API")
public interface UserAuthApi {

    @Operation(
            summary = "회원가입",
            description = "새로운 사용자를 생성합니다. \n" +
                    "이메일 중복 체크 후 비밀번호 암호화하여 저장합니다. \n" +
                    "회원가입 성공 시 사용자 정보를 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원 가입 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = dgu.umc_app.global.response.ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 응답",
                                    value = """
                        {
                            "errorCode": null,
                            "message": "OK",
                            "result": {
                                "accessToken": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI...",
                                "refreshToken": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUz...",
                                "tokenType": "Bearer",
                                "accessTokenExpiresIn": 3600,
                                "refreshTokenExpiresIn": 604800
                            }
                        }
                        """
                            )
                    )
            ),
            @ApiResponse(responseCode = "409", description = "중복 이메일",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "중복 이메일",
                                            summary = "중복 이메일",
                                            value = """
                            {
                                "status": 409,
                                "errorCode": "USER409_2",
                                "message": "해당 이메일이 존재합니다."
                            }
                            """
                                    )}
                    )
            ),
            @ApiResponse(responseCode = "400", description = "입력값 검증 실패",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "입력값 검증 실패",
                                    value = """
                        {
                            "status": 400,
                            "errorCode": "COMMON_002",
                            "message": "입력값 검증에 실패했습니다."
                        }
                        """
                            )
                    )
            )
    })
    UserResponse signup(@Valid @RequestBody UserSignupRequest request);

    @Operation(
            summary = "이메일 중복 확인",
            description = "회원가입 시 이메일 중복 여부를 확인합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "사용 가능한 이메일",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = dgu.umc_app.global.response.ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 응답",
                                    value = """
                    {
                      "errorCode" : null,
                      "message" : "OK",
                      "result" : null
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "409", description = "이미 존재하는 이메일",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "이메일 중복",
                                    value = """
                    {
                        "status" : 409,
                        "errorCode" : "USER_003",
                        "message" : "이미 존재하는 이메일입니다"
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "입력값 검증 실패",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "검증 실패",
                                    value = """
                    {
                        "status" : 400,
                        "errorCode" : "COMMON_002",
                        "message" : "입력값 검증에 실패했습니다"
                    }
                    """
                            )
                    )
            )
    })
    void duplicateCheck(@Parameter(example = "dhzktldh@gmail.com") @RequestParam @Email String email);

    @Operation(
            summary = "로그인",
            description = "사용자 로그인을 처리합니다. \n" +
                    "이메일과 비밀번호 검증 후 JWT 토큰을 발급합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = dgu.umc_app.global.response.ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 응답",
                                    value = """
                        {
                            "errorCode": null,
                            "message": "OK",
                            "result": {
                                "accessToken": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI...",
                                "refreshToken": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUz...",
                                "tokenType": "Bearer",
                                "accessTokenExpiresIn": 3600,
                                "refreshTokenExpiresIn": 604800
                            }
                        }
                        """
                            )
                    )
            ),
            @ApiResponse(responseCode = "401", description = "패스워드가 일치하지 않는 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "패스워드 불일치",
                                    value = """
                        {
                            "status": 401,
                            "errorCode": "USER401_3",
                            "message": "비밀번호가 틀립니다."
                        }
                        """
                            )
                    )
            ),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 이메일인 경우",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "사용자 없음",
                                    value = """
                        {
                            "status": 404,
                            "errorCode": "USER404_1",
                            "message": "해당 사용자가 존재하지 않습니다."
                        }
                        """
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "입력값 검증 실패",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "검증 실패",
                                    value = """
                        {
                            "status" : 400,
                            "errorCode" : "COMMON_002",
                            "message" : "입력값 검증에 실패했습니다"
                        }
                        """
                            )
                    )
            )
    })
    UserResponse login(@Valid @RequestBody UserLoginRequest request);

    @Operation(
            summary = "구글 로그인",
            description = "구글 ID 토큰을 받아 로그인/회원가입 및 JWT 발급"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "구글 로그인 성공 또는 회원가입 필요",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = dgu.umc_app.global.response.ApiResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "로그인 성공",
                                            summary = "기존 사용자 로그인 성공",
                                            value = """
                            {
                                "errorCode": null,
                                "message": "OK",
                                "result": {
                                    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
                                    "refreshToken": "eyJhbGciOiJIUzI1NiJ...",
                                    "tokenType": "Bearer",
                                    "accessTokenExpiresIn": 3600,
                                    "refreshTokenExpiresIn": 604800,
                                    "isNewUser": false,
                                    "isPending": false
                                }
                            }
                            """
                                    ),
                                    @ExampleObject(
                                            name = "회원가입 필요",
                                            summary = "신규 사용자 또는 추가 정보 입력 필요",
                                            value = """
                            {
                                "errorCode": null,
                                "message": "OK",
                                "result": {
                                    "accessToken": "eyJhbGciOiJIUzI1NiJ9.tempTokenForSignup...",
                                    "refreshToken": null,
                                    "tokenType": "Bearer",
                                    "accessTokenExpiresIn": 300,
                                    "refreshTokenExpiresIn": null,
                                    "isNewUser": true,
                                    "isPending": true
                                }
                            }
                            """
                                    )
                            }
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Google OAuth 인증 실패",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "소셜 토큰 검증 실패",
                                    value = """
                    {
                        "status" : 400,
                        "errorCode" : "USER400_4",
                        "message" : "유효하지 않은 소셜 로그인 토큰입니다."
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "401", description = "유효하지 않은 Google ID 토큰",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "토큰 검증 실패",
                                    value = """
                    {
                        "status" : 401,
                        "errorCode" : "AUTH401_1",
                        "message" : "유효하지 않은 토큰입니다."
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "500", description = "외부 API 호출 실패",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "외부 API 오류",
                                    value = """
                    {
                        "status" : 500,
                        "errorCode" : "SERVER_003",
                        "message" : "외부 API 호출에 실패했습니다."
                    }
                    """
                            )
                    )
            )
    })
    AuthLoginResponse googleLogin(@Valid @RequestBody GoogleLoginRequest request);

    @Operation(
            summary = "카카오 로그인",
            description = "카카오 Access Token을 받아 로그인/회원가입 및 JWT 발급"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "카카오 로그인 성공 또는 회원가입 필요",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = dgu.umc_app.global.response.ApiResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "로그인 성공",
                                            summary = "기존 사용자 로그인 성공",
                                            value = """
                            {
                                "errorCode": null,
                                "message": "OK",
                                "result": {
                                    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
                                    "refreshToken": "eyJhbGciOiJIUzI1NiJ...",
                                    "tokenType": "Bearer",
                                    "accessTokenExpiresIn": 3600,
                                    "refreshTokenExpiresIn": 604800,
                                    "isNewUser": false,
                                    "isPending": false
                                }
                            }
                            """
                                    ),
                                    @ExampleObject(
                                            name = "회원가입 필요",
                                            summary = "신규 사용자 또는 추가 정보 입력 필요",
                                            value = """
                            {
                                "errorCode": null,
                                "message": "OK",
                                "result": {
                                    "accessToken": "eyJhbGciOiJIUzI1NiJ9.tempTokenForSignup...",
                                    "refreshToken": null,
                                    "tokenType": "Bearer",
                                    "accessTokenExpiresIn": 300,
                                    "refreshTokenExpiresIn": null,
                                    "isNewUser": true,
                                    "isPending": true
                                }
                            }
                            """
                                    )
                            }
                    )
            ),
            @ApiResponse(responseCode = "400", description = "카카오 OAuth 인증 실패",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "소셜 토큰 검증 실패",
                                    value = """
                    {
                        "status" : 400,
                        "errorCode" : "USER400_4",
                        "message" : "유효하지 않은 소셜 로그인 토큰입니다."
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "500", description = "외부 API 호출 실패",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "외부 API 오류",
                                    value = """
                    {
                        "status" : 500,
                        "errorCode" : "SERVER_003",
                        "message" : "외부 API 호출에 실패했습니다."
                    }
                    """
                            )
                    )
            )
    })
    AuthLoginResponse kakaoLogin(@Valid @RequestBody KakaoLoginRequest request);

    @Operation(
            summary = "구글 회원가입 완료",
            description = "구글 로그인 후 추가 정보 입력으로 회원가입 완료"
    )
    @SecurityRequirement(name = "JWT")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원가입 완료",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = dgu.umc_app.global.response.ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 응답",
                                    description = "구글 회원가입 완료 후 JWT 토큰 발급",
                                    value = """
                    {
                        "errorCode": null,
                        "message": "OK",
                        "result": {
                            "accessToken": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI...",
                            "refreshToken": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUz...",
                            "tokenType": "Bearer",
                            "accessTokenExpiresIn": 3600,
                            "refreshTokenExpiresIn": 604800
                        }
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "입력값 검증 실패 또는 이미 완료된 회원가입",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "입력값 검증 실패",
                                            summary = "입력값 검증 실패",
                                            value = """
                            {
                                "status": 400,
                                "errorCode": "COMMON_002",
                                "message": "입력값 검증에 실패했습니다."
                            }
                            """
                                    ),
                                    @ExampleObject(
                                            name = "이미 완료된 회원가입",
                                            summary = "PENDING 상태가 아닌 사용자",
                                            value = """
                            {
                                "status": 400,
                                "errorCode": "AUTH400_5",
                                "message": "이미 회원가입이 완료된 사용자입니다."
                            }
                            """
                                    )
                            }
                    )
            ),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "인증 실패",
                                    value = """
                    {
                        "status": 401,
                        "errorCode": "COMMON_003",
                        "message": "인증이 필요합니다."
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "사용자 없음",
                                    value = """
                    {
                        "status": 404,
                        "errorCode": "USER404_1",
                        "message": "해당 사용자가 존재하지 않습니다."
                    }
                    """
                            )
                    )
            )
    })
    UserResponse googleSignUp(@Valid @RequestBody GoogleSignUpRequest request, @LoginUser Long userId);

    @Operation(
            summary = "카카오 회원가입 완료",
            description = "카카오 로그인 후 추가 정보 입력으로 회원가입 완료"
    )
    @SecurityRequirement(name = "JWT")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원가입 완료",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = dgu.umc_app.global.response.ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 응답",
                                    description = "카카오 회원가입 완료 후 JWT 토큰 발급",
                                    value = """
                    {
                        "errorCode": null,
                        "message": "OK",
                        "result": {
                            "accessToken": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI...",
                            "refreshToken": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUz...",
                            "tokenType": "Bearer",
                            "accessTokenExpiresIn": 3600,
                            "refreshTokenExpiresIn": 604800
                        }
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "입력값 검증 실패 또는 이미 완료된 회원가입",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "입력값 검증 실패",
                                            summary = "입력값 검증 실패",
                                            value = """
                            {
                                "status": 400,
                                "errorCode": "COMMON_002",
                                "message": "입력값 검증에 실패했습니다."
                            }
                            """
                                    ),
                                    @ExampleObject(
                                            name = "이미 완료된 회원가입",
                                            summary = "PENDING 상태가 아닌 사용자",
                                            value = """
                            {
                                "status": 400,
                                "errorCode": "AUTH400_5",
                                "message": "이미 회원가입이 완료된 사용자입니다."
                            }
                            """
                                    )
                            }
                    )
            ),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "인증 실패",
                                    value = """
                    {
                        "status": 401,
                        "errorCode": "COMMON_003",
                        "message": "인증이 필요합니다."
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "사용자 없음",
                                    value = """
                    {
                        "status": 404,
                        "errorCode": "USER404_1",
                        "message": "해당 사용자가 존재하지 않습니다."
                    }
                    """
                            )
                    )
            )
    })
    UserResponse kakaoSignUp(@Valid @RequestBody KakaoSignUpRequest request, @LoginUser Long userId);

    @Operation(
            summary = "로그아웃",
            description = "사용자 로그아웃을 처리합니다. \n" +
                    "현재 사용자의 액세스 토큰을 블랙리스트에 추가하고 리프레시 토큰을 삭제합니다."
    )
    @SecurityRequirement(name = "JWT")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그아웃 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = dgu.umc_app.global.response.ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 응답",
                                    value = """
                    {
                        "errorCode": null,
                        "message": "OK",
                        "result": null
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "사용자 인증 실패",
                                            summary = "인증되지 않은 사용자",
                                            value = """
                            {
                                "status": 401,
                                "errorCode": "AUTH401_6",
                                "message": "사용자가 인증되지 않았습니다."
                            }
                            """
                                    ),
                                    @ExampleObject(
                                            name = "유효하지 않은 토큰",
                                            summary = "액세스 토큰이 유효하지 않음",
                                            value = """
                            {
                                "status": 401,
                                "errorCode": "AUTH401_1",
                                "message": "유효하지 않은 토큰입니다."
                            }
                            """
                                    )
                            }
                    )
            ),
            @ApiResponse(responseCode = "500", description = "로그아웃 처리 실패",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "로그아웃 처리 실패",
                                    value = """
                    {
                        "status": 500,
                        "errorCode": "AUTH500_7",
                        "message": "로그아웃 처리 중 오류가 발생했습니다."
                    }
                    """
                            )
                    )
            )
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
                            schema = @Schema(implementation = dgu.umc_app.global.response.ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 응답",
                                    value = """
                    {
                        "errorCode": null,
                        "message": "OK",
                        "result": {
                            "accessToken": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI...",
                            "refreshToken": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUz...",
                            "tokenType": "Bearer",
                            "accessTokenExp": 3600,
                            "refreshTokenExp": 604800
                        }
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "입력값 검증 실패",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "입력값 검증 실패",
                                    value = """
                    {
                        "status": 400,
                        "errorCode": "COMMON_002",
                        "message": "입력값 검증에 실패했습니다."
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "401", description = "리프레시 토큰 관련 오류",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "유효하지 않은 리프레시 토큰",
                                            summary = "만료되거나 잘못된 리프레시 토큰",
                                            value = """
                            {
                                "status": 401,
                                "errorCode": "AUTH401_2",
                                "message": "유효하지 않은 리프레시 토큰입니다."
                            }
                            """
                                    ),
                                    @ExampleObject(
                                            name = "리프레시 토큰을 찾을 수 없음",
                                            summary = "Redis에 저장된 리프레시 토큰이 없음",
                                            value = """
                            {
                                "status": 401,
                                "errorCode": "AUTH401_3",
                                "message": "리프레시 토큰을 찾을 수 없습니다."
                            }
                            """
                                    ),
                                    @ExampleObject(
                                            name = "리프레시 토큰 불일치",
                                            summary = "요청한 토큰과 저장된 토큰이 다름",
                                            value = """
                            {
                                "status": 401,
                                "errorCode": "AUTH401_4",
                                "message": "리프레시 토큰이 일치하지 않습니다."
                            }
                            """
                                    ),
                                    @ExampleObject(
                                            name = "사용자 인증 실패",
                                            summary = "토큰의 사용자 ID로 사용자를 찾을 수 없음",
                                            value = """
                            {
                                "status": 401,
                                "errorCode": "AUTH401_6",
                                "message": "사용자가 인증되지 않았습니다."
                            }
                            """
                                    )
                            }
                    )
            )
    })
    ReissueTokenResponse reissueToken(@Valid @RequestBody ReissueTokenRequest request);

    @Operation(
            summary = "설문조사 완료",
            description = "사용자 설문조사 응답을 저장합니다. \n" +
                    "미루는 상황(다중선택), 정도(단일선택), 이유(다중선택)에 대한 응답을 enum 기반으로 검증하고 " +
                    "한글 설명으로 DB에 저장하며, 사용자 상태를 ACTIVE로 변경합니다. \n" +
                    "요청은 문자열(PHONE, NORMAL, PERFECTIONISM...) 형태로 가능합니다."
    )
    @SecurityRequirement(name = "JWT")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "설문 완료 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = dgu.umc_app.global.response.ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 응답",
                                    value = """
                    {
                        "errorCode": null,
                        "message": "OK",
                        "result": {
                            "message": "설문조사가 완료되었습니다!",
                            "completedAt": "2025-08-13T15:30:45.123456",
                            "status": "COMPLETED"
                        }
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "입력값 검증 실패",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "입력값 검증 실패",
                                    value = """
                    {
                        "status": 400,
                        "errorCode": "COMMON_002",
                        "message": "입력값 검증에 실패했습니다."
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "인증 실패",
                                    value = """
                    {
                        "status": 401,
                        "errorCode": "COMMON_003",
                        "message": "인증이 필요합니다."
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "사용자 없음",
                                    value = """
                    {
                        "status": 404,
                        "errorCode": "USER404_1",
                        "message": "해당 사용자가 존재하지 않습니다."
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "409", description = "이미 설문을 완료한 사용자",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "중복 설문 완료",
                                    value = """
                    {
                        "status": 409,
                        "errorCode": "USER409_9",
                        "message": "이미 설문조사를 완료한 사용자입니다."
                    }
                    """
                            )
                    )
            )
    })
    SurveyResponse survey(@Valid @RequestBody SurveyRequest request, @LoginUser Long userId);

    @Operation(
            summary = "회원 탈퇴",
            description = "현재 로그인한 사용자의 회원 탈퇴를 처리합니다. \n" +
                    "소프트 삭제 방식으로 처리되며, 모든 토큰이 무효화됩니다."
    )
    @SecurityRequirement(name = "JWT")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원 탈퇴 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = dgu.umc_app.global.response.ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 응답",
                                    value = """
                    {
                        "errorCode": null,
                        "message": "OK",
                        "result": null
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "이미 탈퇴한 사용자",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "이미 탈퇴한 사용자",
                                    value = """
                    {
                        "status": 400,
                        "errorCode": "USER400_4",
                        "message": "이미 탈퇴한 사용자입니다."
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "인증 실패",
                                    value = """
                    {
                        "status": 401,
                        "errorCode": "COMMON_003",
                        "message": "인증이 필요합니다."
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "사용자 없음",
                                    value = """
                    {
                        "status": 404,
                        "errorCode": "USER404_1",
                        "message": "해당 사용자가 존재하지 않습니다."
                    }
                    """
                            )
                    )
            )
    })
    void withdrawUser(@LoginUser Long userId);

    @Operation(
            summary = "비밀번호 변경",
            description = "현재 비밀번호를 확인하고 새 비밀번호로 변경합니다. \n" +
                    "비밀번호 변경 후 모든 토큰이 무효화되어 재로그인이 필요합니다. \n" +
                    "현재 설정되어 있는 비밀번호를 아는 경우 사용합니다."
    )
    @SecurityRequirement(name = "JWT")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "비밀번호 변경 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = dgu.umc_app.global.response.ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 응답",
                                    value = """
                    {
                        "errorCode": null,
                        "message": "OK",
                        "result": null
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "비밀번호 관련 오류",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "입력값 검증 실패",
                                            summary = "비밀번호 형식 오류",
                                            value = """
                            {
                                "status": 400,
                                "errorCode": "COMMON_002",
                                "message": "입력값 검증에 실패했습니다."
                            }
                            """
                                    ),
                                    @ExampleObject(
                                            name = "동일한 비밀번호",
                                            summary = "새 비밀번호가 현재 비밀번호와 동일",
                                            value = """
                            {
                                "status": 400,
                                "errorCode": "USER400_6",
                                "message": "새 비밀번호는 현재 비밀번호와 달라야 합니다."
                            }
                            """
                                    ),
                                    @ExampleObject(
                                            name = "소셜 로그인 사용자",
                                            summary = "소셜 로그인 사용자의 비밀번호 변경 시도",
                                            value = """
                            {
                                "status": 400,
                                "errorCode": "USER400_7",
                                "message": "소셜 로그인 사용자는 비밀번호 변경이 불가능합니다."
                            }
                            """
                                    )
                            }
                    )
            ),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "현재 비밀번호 불일치",
                                            summary = "입력한 현재 비밀번호가 틀림",
                                            value = """
                            {
                                "status": 401,
                                "errorCode": "USER401_3",
                                "message": "비밀번호가 틀립니다."
                            }
                            """
                                    ),
                                    @ExampleObject(
                                            name = "인증 실패",
                                            summary = "JWT 토큰 인증 실패",
                                            value = """
                            {
                                "status": 401,
                                "errorCode": "COMMON_003",
                                "message": "인증이 필요합니다."
                            }
                            """
                                    )
                            }
                    )
            ),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "사용자 없음",
                                    value = """
                    {
                        "status": 404,
                        "errorCode": "USER404_1",
                        "message": "해당 사용자가 존재하지 않습니다."
                    }
                    """
                            )
                    )
            )
    })
    void changePassword(@LoginUser Long userId, @Valid @RequestBody ChangePasswordRequest request);


    @Operation(
            summary = "비밀번호 재설정 요청",
            description = "이메일로 비밀번호 재설정 인증 코드가 전송됩니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "인증 코드 전송 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = dgu.umc_app.global.response.ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 응답",
                                    value = """
                    {
                        "errorCode": null,
                        "message": "OK",
                        "result": null
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "입력값 검증 실패",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "입력값 검증 실패",
                                    value = """
                    {
                        "status": 400,
                        "errorCode": "COMMON_002",
                        "message": "입력값 검증에 실패했습니다."
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "404", description = "해당 이메일로 가입된 사용자가 없음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "이메일로 가입된 사용자 없음",
                                    value = """
                    {
                        "status": 404,
                        "errorCode": "USER404_8",
                        "message": "해당 이메일로 가입된 사용자가 존재하지 않습니다."
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "500", description = "이메일 전송 실패",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "이메일 전송 실패",
                                    value = """
                    {
                        "status": 500,
                        "errorCode": "USER500_13",
                        "message": "이메일 전송에 실패했습니다."
                    }
                    """
                            )
                    )
            )
    })
    void requestPasswordReset(@Valid @RequestBody PasswordResetRequest request);

    @Operation(
            summary = "비밀번호 재설정 코드 검증",
            description = "이메일로 전송된 인증 코드를 검증하고 리셋 토큰을 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "코드 검증 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = dgu.umc_app.global.response.ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 응답",
                                    value = """
                    {
                        "errorCode": null,
                        "message": "OK",
                        "result": {
                            "resetToken": "eyJhbGciOiJIUzI1NiJ9.resetTokenExample..."
                        }
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "인증 코드 관련 오류",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "입력값 검증 실패",
                                            summary = "이메일 형식 오류 등",
                                            value = """
                            {
                                "status": 400,
                                "errorCode": "COMMON_002",
                                "message": "입력값 검증에 실패했습니다."
                            }
                            """
                                    ),
                                    @ExampleObject(
                                            name = "인증 코드 만료",
                                            summary = "Redis에서 인증 코드가 만료됨",
                                            value = """
                            {
                                "status": 400,
                                "errorCode": "USER400_10",
                                "message": "인증 코드가 만료되었습니다."
                            }
                            """
                                    ),
                                    @ExampleObject(
                                            name = "유효하지 않은 인증 코드",
                                            summary = "인증 코드가 일치하지 않음",
                                            value = """
                            {
                                "status": 400,
                                "errorCode": "USER400_9",
                                "message": "유효하지 않은 인증 코드입니다."
                            }
                            """
                                    )
                            }
                    )
            )
    })
    VerifyResponse verifyResetCode(@Valid @RequestBody VerifyResetCodeRequest request);

    @Operation(
            summary = "비밀번호 재설정",
            description = "검증된 토큰으로 새 비밀번호를 설정합니다. \n" +
                    "현재 설정되어 있는 비밀번호를 모를때 사용합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "비밀번호 재설정 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = dgu.umc_app.global.response.ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 응답",
                                    value = """
                    {
                        "errorCode": null,
                        "message": "OK",
                        "result": null
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "토큰 만료 또는 입력값 검증 실패",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "입력값 검증 실패",
                                            summary = "비밀번호 형식 오류",
                                            value = """
                            {
                                "status": 400,
                                "errorCode": "COMMON_002",
                                "message": "입력값 검증에 실패했습니다."
                            }
                            """
                                    ),
                                    @ExampleObject(
                                            name = "리셋 토큰 만료",
                                            summary = "Reset-Token이 만료되거나 유효하지 않음",
                                            value = """
                            {
                                "status": 400,
                                "errorCode": "USER400_14",
                                "message": "토큰이 만료되었습니다."
                            }
                            """
                                    ),
                                    @ExampleObject(
                                            name = "소셜 로그인 사용자",
                                            summary = "소셜 로그인 사용자의 비밀번호 재설정 시도",
                                            value = """
                            {
                                "status": 400,
                                "errorCode": "USER400_7",
                                "message": "소셜 로그인 사용자는 비밀번호 변경이 불가능합니다."
                            }
                            """
                                    )
                            }
                    )
            ),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "사용자 없음",
                                    value = """
                    {
                        "status": 404,
                        "errorCode": "USER404_1",
                        "message": "해당 사용자가 존재하지 않습니다."
                    }
                    """
                            )
                    )
            )
    })
    void resetPassword(
            @Parameter(
                    name = "Reset-Token",
                    description = "비밀번호 재설정을 위한 검증된 토큰",
                    required = true,
                    example = "eyJhbGciOiJIUzI1NiJ9.resetTokenExample...",
                    in = io.swagger.v3.oas.annotations.enums.ParameterIn.HEADER
            )
            @RequestHeader("Reset-Token") String resetToken,
            @Valid @RequestBody ResetPasswordRequest request
    );
} 