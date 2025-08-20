package dgu.umc_app.domain.user.controller;

import dgu.umc_app.domain.user.dto.request.AccountUpdateRequest;
import dgu.umc_app.domain.user.dto.request.SurveyRequest;
import dgu.umc_app.domain.user.dto.response.PeanutCountResponse;
import dgu.umc_app.domain.user.dto.response.SurveyResponse;
import dgu.umc_app.domain.user.dto.response.UserInfoResponse;
import dgu.umc_app.domain.user.dto.response.UserSurveyResponse;
import dgu.umc_app.domain.user.entity.ProfileImage;
import dgu.umc_app.global.authorize.LoginUser;
import dgu.umc_app.global.exception.CustomErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "user", description = "사용자 관련 API")
public interface UserApi {

    @Operation(
            summary = "개인정보 조회 API",
            description = "마이페이지 개인정보 조회 API 입니다."
    )
    @SecurityRequirement(name = "JWT")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "개인정보 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = dgu.umc_app.global.response.ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 응답",
                                    value = """
                    {
                        "errorCode": null,
                        "message": "OK",
                        "result": {
                            "id": 1,
                            "name": "추상윤",
                            "birthday": "2003-03-03",
                            "email": "dhzktldh@gmail.com",
                            "phoneNumber": "010-7689-3141",
                            "nickname": "추추",
                            "agreedPrivacyPolicy": true,
                            "oauthProvider": "GOOGLE",
                            "profileImage": "PROFILE_1"
                        }
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
    UserInfoResponse getUserInfo(@LoginUser Long userId);

    @Operation(
            summary = "프로필사진, 닉네임 변경 API",
            description = """
                    프로필 사진은 색상으로 구분합니다. (YELLOW, GREEN, BLUE, BEIGE, PINK, RED)
                    """
    )
    @SecurityRequirement(name = "JWT")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "프로필사진 변경 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = dgu.umc_app.global.response.ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 응답",
                                    value = """
                    {
                        "errorCode": null,
                        "message": "OK",
                        "result": {
                            "id": 1,
                            "name": "추상윤",
                            "birthday": "2003-03-03",
                            "email": "dhzktldh@gmail.com",
                            "phoneNumber": "010-7689-3141",
                            "nickname": "추추",
                            "agreedPrivacyPolicy": true,
                            "oauthProvider": "GOOGLE",
                            "profileImage": "PROFILE_2"
                        }
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
    UserInfoResponse updateProfile(@LoginUser Long userId, ProfileImage profileImage, String nickname);

    @Operation(
            summary = "땅콩 개수 조회 API",
            description = "땅콩 개수 조회 API 입니다."
    )
    @SecurityRequirement(name = "JWT")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "땅콩 개수 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = dgu.umc_app.global.response.ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 응답",
                                    value = """
                    {
                        "errorCode": null,
                        "message": "OK",
                        "result": {
                            "peanutCount": 150
                        }
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
    PeanutCountResponse getPeanutCount(@LoginUser Long userId);

    @Operation(
            summary = "설문조사 결과 조회 API",
            description = "사용자의 설문조사 결과를 조회하는 API 입니다."
    )
    @SecurityRequirement(name = "JWT")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "설문조사 결과 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = dgu.umc_app.global.response.ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 응답",
                                    value = """
                        {
                            "errorCode": null,
                            "message": "OK",
                            "result": {
                                "situationDescriptions": [
                                    "휴대폰을 사용할 때 (SNS, 게임 등)",
                                    "너무 피곤할 때"
                                ],
                                "levelDescription": "보통이다",
                                "reasonDescriptions": [
                                    "완벽하게 해내고 싶어서",
                                    "재미없거나 하기 싫어서"
                                ]
                            }
                        }
                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "설문조사를 완료하지 않음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "설문조사 미완료",
                                    value = """
                    {
                        "status": 400,
                        "errorCode": "USER400_10",
                        "message": "설문조사를 완료하지 않은 사용자입니다."
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
    UserSurveyResponse getUserSurveyResult(@LoginUser Long userId);

    @Operation(
            summary = "설문조사 응답 수정 API",
            description = "사용자의 설문 응답을 수정합니다. 한 번 제출한 뒤에도 설문 응답을 바꿀 수 있습니다."
    )
    @SecurityRequirement(name = "JWT")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "설문조사 응답 수정 성공",
                    content = @Content(mediaType = "applicatio  n/json",
                            schema = @Schema(implementation = dgu.umc_app.global.response.ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "성공 응답",
                                    value = """
                    {
                        "errorCode": null,
                        "message": "OK",
                        "result": {
                            "message": "설문조사가 수정되었습니다!",
                            "completedAt": "2024-01-01T12:00:00",
                            "status": "UPDATED"
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
                                    name = "검증 실패",
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
            )
    })
    SurveyResponse updateSurvey(@LoginUser Long userId, SurveyRequest request);

    @Operation(
            summary = "계정 개인정보 수정 API",
            description = "계정의 개인정보를 수정하는 API 입니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "개인정보 수정 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class))),
    })
    UserInfoResponse updateAccount(@LoginUser Long userId, AccountUpdateRequest accountUpdateRequest);
}
