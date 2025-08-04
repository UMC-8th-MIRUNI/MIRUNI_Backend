package dgu.umc_app.domain.fcm.service;

import com.google.firebase.messaging.FirebaseMessaging;
import dgu.umc_app.domain.plan.repository.AiPlanRepository;
import dgu.umc_app.domain.fcm.dto.request.RegisterFcmTokenRequestDto;
import dgu.umc_app.domain.fcm.dto.request.UpdateFcmNotificationRequestDto;
import dgu.umc_app.domain.fcm.dto.response.RegisterTokenResponseDto;
import dgu.umc_app.domain.fcm.entity.FcmToken;
import dgu.umc_app.domain.fcm.exception.FcmErrorCode;
import dgu.umc_app.domain.fcm.repository.FcmTokenRepository;
import dgu.umc_app.domain.plan.repository.PlanRepository;
import dgu.umc_app.domain.user.entity.User;
import dgu.umc_app.global.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FcmTokenCommandService {

    private final FcmTokenRepository fcmTokenRepository;

    @Transactional
    public RegisterTokenResponseDto registerTokenWithUser(RegisterFcmTokenRequestDto request, User user) {
        FcmToken savedFcmtoken = fcmTokenRepository.save(request.toEntity(user));
        return RegisterTokenResponseDto.of(savedFcmtoken.getId());
    }

    @Transactional
    public void updateFcmNotification(UpdateFcmNotificationRequestDto request, User user){


        FcmToken fcmToken = fcmTokenRepository.findByUserAndDeviceId(user, request.deviceId())
                        .orElseThrow(() -> BaseException.type(FcmErrorCode.NOT_FOUND_FCM_TOKEN));

        fcmToken.updateNotificationEnabled(request.enabled());
    }


}
