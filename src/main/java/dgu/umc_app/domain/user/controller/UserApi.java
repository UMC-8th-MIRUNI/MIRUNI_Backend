package dgu.umc_app.domain.user.controller;

import dgu.umc_app.domain.user.dto.request.AccountUpdateRequest;
import dgu.umc_app.domain.user.dto.response.PeanutCountResponse;
import dgu.umc_app.domain.user.dto.response.UserInfoResponse;
import dgu.umc_app.domain.user.dto.response.UserSurveyResponse;
import dgu.umc_app.domain.user.entity.ProfileImage;
import dgu.umc_app.global.authorize.LoginUser;
import dgu.umc_app.global.exception.CustomErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "user", description = "사용자 관련 API")
public interface UserApi {

    @Operation(
            summary = "개인정보 조회 API",
            description = "마이페이지 개인정보 조회 API 입니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "개인정보 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class)))
    })
    UserInfoResponse getUserInfo(@LoginUser Long userId);

    @Operation(
            summary = "프로필사진, 닉네임 변경 API",
            description = """
                    프로필 사진은 색상으로 구분합니다. (YELLOW, GREEN, BLUE, BEIGE, PINK, RED)
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "프로필사진 변경 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class)))
    })
    UserInfoResponse updateProfile(@LoginUser Long userId, ProfileImage profileImage, String nickname);
  
  
    @Operation(
            summary = "땅콩 개수 조회 API",
            description = "땅콩 개수 조회 API 입니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "땅콩 개수 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class)))
    })
    PeanutCountResponse getPeanutCount(@LoginUser Long userId);

    @Operation(
            summary = "설문조사 결과 조회 API",
            description = "사용자의 설문조사 결과를 조회하는 API 입니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "설문조사 결과 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "설문조사를 완료하지 않음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class)))
    })
    UserSurveyResponse getUserSurveyResult(@LoginUser Long userId);

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
