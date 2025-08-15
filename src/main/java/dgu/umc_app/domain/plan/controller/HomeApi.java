package dgu.umc_app.domain.plan.controller;

import dgu.umc_app.domain.plan.dto.response.HomeResponse;
import dgu.umc_app.global.authorize.LoginUser;
import dgu.umc_app.global.exception.CustomErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "user", description = "사용자 관련 API")
public interface HomeApi {

    @Operation(
            summary = "홈 화면 조회 API",
            description = "메인 홈 화면 조회 API 입니다."
    )
    @SecurityRequirement(name = "JWT")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "홈 화면 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = dgu.umc_app.global.response.ApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class)))
    })
    HomeResponse getHomePage(@LoginUser Long userId);
}
