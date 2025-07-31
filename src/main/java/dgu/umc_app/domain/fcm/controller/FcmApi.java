package dgu.umc_app.domain.fcm.controller;

import dgu.umc_app.domain.fcm.dto.request.RegisterFcmTokenRequestDto;
import dgu.umc_app.domain.fcm.dto.request.UpdateFcmNotificationRequestDto;
import dgu.umc_app.domain.fcm.dto.response.RegisterTokenResponseDto;
import dgu.umc_app.global.exception.CustomErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "FCM", description = "FCM 푸시 알림 관리 API")
public interface FcmApi {

    @Operation(
            summary = "FCM 토큰 등록",
            description = """
                    사용자의 FCM 토큰을 등록하여 푸시 알림을 받을 수 있도록 설정합니다.
            
                    주의사항:
                    - 동일한 deviceId로 재등록 시 기존 토큰이 업데이트됩니다
                    - JWT 인증이 필요합니다
                    """,
            tags = {"FCM"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "FCM 토큰 등록 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "GlobalResponseAdvice로 감싸진 등록 성공 응답",
                                    value = """
                                            {
                                                "errorCode": null,
                                                "message": "OK",
                                                "result": {
                                                    "tokenId": 123
                                                }
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 데이터",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "잘못된 요청",
                                    value = """
                                            {
                                                "status": 400,
                                                "code": "INVALID_REQUEST",
                                                "message": "FCM 토큰이 비어있습니다"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 사용자",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "인증 실패",
                                    value = """
                                            {
                                                "status": 401,
                                                "code": "UNAUTHORIZED",
                                                "message": "인증이 필요합니다"
                                            }
                                            """
                            )
                    )
            )
    })
    RegisterTokenResponseDto registerToken(
            @Valid @RequestBody RegisterFcmTokenRequestDto request,
            @Parameter(hidden = true) Authentication authentication
    );

    @Operation(
            summary = "FCM 토큰 상태 업데이트",
            description = """
                    사용자의 FCM 상태를 설정하여 활성화 시키거나 비활성화 시킵니다
            
                    주의사항:
                    - JWT 인증이 필요합니다
                    """,
            tags = {"FCM"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "FCM 토큰 상태 update 완료",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "GlobalResponseAdvice로 감싸진 등록 성공 응답",
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
            @ApiResponse(
                    responseCode = "404",
                    description = "잘못된 요청 데이터",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "잘못된 요청",
                                    value = """
                                            {
                                                "status": 404,
                                                "code": "FCM002",
                                                "message": "FCM 토큰을 찾을 수 없습니다."
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 사용자",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "인증 실패",
                                    value = """
                                            {
                                                "status": 401,
                                                "code": "UNAUTHORIZED",
                                                "message": "인증이 필요합니다"
                                            }
                                            """
                            )
                    )
            )
    })
    void updateFcmNotification(
            @RequestBody @Valid UpdateFcmNotificationRequestDto request,
            @Parameter(hidden = true) Authentication authentication
    );

}