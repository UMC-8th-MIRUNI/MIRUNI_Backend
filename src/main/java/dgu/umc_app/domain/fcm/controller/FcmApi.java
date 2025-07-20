package dgu.umc_app.domain.fcm.controller;

import dgu.umc_app.domain.fcm.dto.request.FcmTokenRegisterRequestDto;
import dgu.umc_app.domain.fcm.dto.request.BannerNotificationSendRequestDto;
import dgu.umc_app.domain.fcm.dto.response.FcmTokenRegisterResponseDto;
import dgu.umc_app.global.exception.CustomErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "fcm", description = "FCM 관련 API")
public interface FcmApi {

    @Operation(
            summary = "FCM 토큰 등록 API",
            description = "사용자의 FCM 토큰을 등록하여 푸시 알림을 받을 수 있도록 설정합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "FCM 토큰 등록 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = FcmTokenRegisterResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class)))
    })
    FcmTokenRegisterResponseDto registerToken(@Valid @RequestBody FcmTokenRegisterRequestDto request,
                                             Authentication authentication);

    @Operation(
            summary = "배너 알림 전송 API",
            description = "Plan 또는 AiPlan에 대한 리마인더 알림을 전송합니다. (1시간 전, 10분 전)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "알림 전송 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Plan 또는 AiPlan을 찾을 수 없음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class)))
    })
    ResponseEntity<Void> sendBannerNotification(@Valid @RequestBody BannerNotificationSendRequestDto request);

    @Operation(
            summary = "활성화된 FCM 토큰 조회 API",
            description = "현재 사용자의 활성화된 FCM 토큰 목록을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "토큰 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CustomErrorResponse.class)))
    })
    ResponseEntity<java.util.List<String>> getActiveTokens();
}
