package dgu.umc_app.domain.user.controller;

import dgu.umc_app.domain.user.dto.response.UserInfoResponse;
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
            summary = "프로필사진 변경 API",
            description = "프로필 사진 변경 API 입니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "프로필사진 변경 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class)))
    })
    UserInfoResponse updateProfileImage(@LoginUser Long userId, ProfileImage profileImage);
}
