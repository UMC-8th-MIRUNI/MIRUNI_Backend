package dgu.umc_app.domain.fcm.controller;

import dgu.umc_app.domain.fcm.dto.request.FcmTokenRegisterRequestDto;
import dgu.umc_app.domain.fcm.dto.request.BannerNotificationSendRequestDto;
import dgu.umc_app.domain.fcm.dto.response.FcmTokenRegisterResponseDto;
import dgu.umc_app.domain.fcm.service.FcmTokenCommandService;
import dgu.umc_app.domain.fcm.service.FcmTokenQueryService;
import dgu.umc_app.domain.user.entity.User;
import dgu.umc_app.global.authorize.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/fcm")
public class FcmController implements FcmApi {

    private final FcmTokenCommandService fcmTokenCommandService;
    private final FcmTokenQueryService fcmTokenQueryService;

    @Override
    @PostMapping("/token")
    public FcmTokenRegisterResponseDto registerToken(FcmTokenRegisterRequestDto request,
                                                    Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User currentUser = userDetails.getUser();
        
        log.info("FCM 토큰 등록 요청 - 사용자 ID: {}, 디바이스 ID: {}", currentUser.getId(), request.deviceId());
        
        return fcmTokenCommandService.registerTokenWithUser(request, currentUser);
    }

    @Override
    @PostMapping("/notification/banner")
    public void sendBannerNotification(BannerNotificationSendRequestDto request) {
        log.info("배너 알림 전송 요청 - 타입: {}, 대상 ID: {}, 리마인더 타입: {}", 
                request.type(), request.targetId(), request.reminderType());
        
        fcmTokenCommandService.sendBannerNotification(request);
        
        log.info("배너 알림 전송 완료 - 타입: {}, 대상 ID: {}", request.type(), request.targetId());

    }

    @Override
    @GetMapping("/tokens")
    public List<String> getActiveTokens() {
        log.info("활성화된 FCM 토큰 조회 요청");
        
        List<String> activeTokens = fcmTokenQueryService.getActiveTokenByUser();
        
        log.info("활성화된 FCM 토큰 조회 완료 - 토큰 개수: {}", activeTokens.size());
        return activeTokens;
    }

}
