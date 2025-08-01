package dgu.umc_app.domain.fcm.controller;

import dgu.umc_app.domain.fcm.dto.request.RegisterFcmTokenRequestDto;
import dgu.umc_app.domain.fcm.dto.request.UpdateFcmNotificationRequestDto;
import dgu.umc_app.domain.fcm.dto.response.RegisterTokenResponseDto;
import dgu.umc_app.domain.fcm.service.FcmTokenCommandService;
import dgu.umc_app.domain.user.entity.User;
import dgu.umc_app.global.authorize.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/fcm")
public class FcmController implements FcmApi {

    private final FcmTokenCommandService fcmTokenCommandService;

    @Override
    @PostMapping("/token")
    public RegisterTokenResponseDto registerToken(RegisterFcmTokenRequestDto request,
                                                  Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User currentUser = userDetails.getUser();
        
        log.info("FCM 토큰 등록 요청 - 사용자 ID: {}, 디바이스 ID: {}", currentUser.getId(), request.deviceId());
        
        return fcmTokenCommandService.registerTokenWithUser(request, currentUser);
    }

    @Override
    @PatchMapping("/notification/setting")
    public void updateFcmNotification(UpdateFcmNotificationRequestDto request, Authentication authentication) {

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User currentUser = userDetails.getUser();

        fcmTokenCommandService.updateFcmNotification(request, currentUser);
    }
}
