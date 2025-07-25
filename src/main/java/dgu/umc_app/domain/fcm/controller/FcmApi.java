package dgu.umc_app.domain.fcm.controller;

import dgu.umc_app.domain.fcm.dto.request.RegisterFcmTokenRequestDto;
import dgu.umc_app.domain.fcm.dto.request.SendBannerNotificationRequestDto;
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
            summary = "배너 알림 전송 (테스트용)",
            description = """
                    Plan 또는 AiPlan에 대한 리마인더 알림을 수동으로 전송합니다.
                    
                    알림 타입
                    - PLAN: 일반 계획 알림
                    - AI_PLAN: AI가 생성한 계획 알림
                    
                    리마인더 타입:
                    - ONE_HOUR_BEFORE: 1시간 전 알림
                    - TEN_MINUTES_BEFORE: 10분 전 알림
                    
                    주의사항
                    - 실제 서비스에서는 스케줄러가 자동으로 처리합니다
                    """,
            tags = {"FCM"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "알림 전송 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "GlobalResponseAdvice로 감싸진 전송 성공",
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
                    responseCode = "400",
                    description = "잘못된 요청 데이터",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "잘못된 타입",
                                    value = """
                                            {
                                                "status": 400,
                                                "code": "INVALID_TYPE",
                                                "message": "지원하지 않는 알림 타입입니다"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Plan 또는 AiPlan을 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "데이터 없음",
                                    value = """
                                            {
                                                "status": 404,
                                                "code": "PLAN_NOT_FOUND",
                                                "message": "해당 ID의 Plan을 찾을 수 없습니다"
                                            }
                                            """
                            )
                    )
            )
    })
    void sendBannerNotification(@Valid @RequestBody SendBannerNotificationRequestDto request);


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