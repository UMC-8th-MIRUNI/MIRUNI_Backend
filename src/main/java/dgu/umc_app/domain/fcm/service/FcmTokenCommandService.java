package dgu.umc_app.domain.fcm.service;

import com.google.firebase.messaging.*;
import dgu.umc_app.domain.ai_plan.entity.AiPlan;
import dgu.umc_app.domain.ai_plan.exception.AiPlanErrorCode;
import dgu.umc_app.domain.ai_plan.repository.AiPlanRepository;
import dgu.umc_app.domain.fcm.dto.request.BannerNotificationSendRequestDto;
import dgu.umc_app.domain.fcm.dto.request.FcmTokenRegisterRequestDto;
import dgu.umc_app.domain.fcm.dto.response.FcmTokenRegisterResponseDto;
import dgu.umc_app.domain.fcm.entity.FcmToken;
import dgu.umc_app.domain.fcm.entity.ReminderType;
import dgu.umc_app.domain.fcm.exception.FcmErrorCode;
import dgu.umc_app.domain.fcm.repository.FcmTokenRepository;
import dgu.umc_app.domain.plan.entity.Plan;
import dgu.umc_app.domain.plan.exception.PlanErrorCode;
import dgu.umc_app.domain.plan.repository.PlanRepository;
import dgu.umc_app.domain.user.entity.User;
import dgu.umc_app.global.authorize.CustomUserDetails;
import dgu.umc_app.global.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FcmTokenCommandService {

    private final FcmTokenRepository fcmTokenRepository;
    private final PlanRepository planRepository;
    private final AiPlanRepository aiPlanRepository;
    private final FcmTokenQueryService fcmTokenQueryService;
    private final FirebaseMessaging firebaseMessaging;


    // Controller에서 사용할 메서드 (User 객체를 직접 받음)
    public FcmTokenRegisterResponseDto registerTokenWithUser(FcmTokenRegisterRequestDto request, User user) {
        FcmToken savedFcmtoken = fcmTokenRepository.save(request.toEntity(user));
        return FcmTokenRegisterResponseDto.of(savedFcmtoken.getId());
    }

    public void sendBannerNotification(BannerNotificationSendRequestDto request) {

        NotificationInfo notificationInfo = createNotificationInfo(request);

        List<String> activeTokens = fcmTokenQueryService.getActiveTokenByUser();

        if(activeTokens.isEmpty()) {
            return;
        }

        sendFcmMessage(activeTokens, notificationInfo, request);



    }

    private NotificationInfo createNotificationInfo(BannerNotificationSendRequestDto request) {
        return switch (request.type()){
            case PLAN -> {
                Plan plan = planRepository.findById(request.targetId())
                        .orElseThrow(() -> BaseException.type(PlanErrorCode.PLAN_NOT_FOUND));

                String title = "할일 알림";
                String body = createPlanMessage(plan, request.reminderType());

                yield new NotificationInfo(plan.getUser().getId(), title, body);
            }
            case AI_PLAN -> {
                AiPlan aiPlan = aiPlanRepository.findById(request.targetId())
                        .orElseThrow(() -> BaseException.type(AiPlanErrorCode.AIPLAN_NOT_FOUND));

                String title = "할일 알림";
                String body = createAiPlanMessage(aiPlan, request.reminderType());

                yield new NotificationInfo(aiPlan.getPlan().getUser().getId(), title, body);
            }
        };
    }

    private String createPlanMessage(Plan plan, ReminderType reminderType) {
        return switch(reminderType){
            case ONE_HOUR_BEFORE -> String.format("1시간 뒤에 <'%s'>가 예정되어 있어!", plan.getTitle());
            case TEN_MINUTES_BEFORE -> String.format("10분 뒤에 <'%s'>가 예정되어 있어!", plan.getTitle());
        };
    }

    private String createAiPlanMessage(AiPlan aiPlan, ReminderType reminderType) {
        return switch(reminderType){
            case ONE_HOUR_BEFORE -> String.format("1시간 뒤에 <'%s'>가 예정되어 있어!", aiPlan.getDescription());
            case TEN_MINUTES_BEFORE -> String.format("10분 뒤에 <'%s'>가 예정되어 있어!", aiPlan.getDescription());
        };
    }

    private void sendFcmMessage(List<String> tokens, NotificationInfo info, BannerNotificationSendRequestDto request) {
        try{
            MulticastMessage message = MulticastMessage.builder()
                    .putData("type", request.type().name())
                    .putData("targetId", request.targetId().toString())
                    .putData("reminderType", request.reminderType().name())
                    .setNotification(Notification.builder()
                            .setTitle(info.title())
                            .setBody(info.body())
                            .build())
                    .addAllTokens(tokens)
                    .build();

            firebaseMessaging.sendEachForMulticast(message);

        } catch(FirebaseMessagingException e) {
            throw BaseException.type(FcmErrorCode.FCM_SEND_FAILED);
        }

    }

    private record NotificationInfo(Long userId, String title, String body){}


    private User getCurrentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getUser();
    }
}
