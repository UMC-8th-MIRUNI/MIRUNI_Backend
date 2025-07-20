package dgu.umc_app.domain.fcm.scheduler;

import dgu.umc_app.domain.ai_plan.entity.AiPlan;
import dgu.umc_app.domain.ai_plan.repository.AiPlanRepository;
import dgu.umc_app.domain.fcm.dto.request.BannerNotificationSendRequestDto;
import dgu.umc_app.domain.fcm.entity.NotificationType;
import dgu.umc_app.domain.fcm.entity.ReminderType;
import dgu.umc_app.domain.fcm.service.FcmTokenCommandService;
import dgu.umc_app.domain.plan.entity.Plan;
import dgu.umc_app.domain.plan.repository.PlanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationScheduler {
    
    private final FcmTokenCommandService fcmTokenCommandService;
    private final PlanRepository planRepository;
    private final AiPlanRepository aiPlanRepository;
    
    @Scheduled(fixedRate = 60000) // 1분마다 실행
    public void checkUpcomingTasks() {
        LocalDateTime now = LocalDateTime.now();
        
        // 1시간 후 시작할 할일들 체크
        checkPlansForReminder(now.plusHours(1), ReminderType.ONE_HOUR_BEFORE);
        checkAiPlansForReminder(now.plusHours(1), ReminderType.ONE_HOUR_BEFORE);
        
        // 10분 후 시작할 할일들 체크  
        checkPlansForReminder(now.plusMinutes(10), ReminderType.TEN_MINUTES_BEFORE);
        checkAiPlansForReminder(now.plusMinutes(10), ReminderType.TEN_MINUTES_BEFORE);
    }
    
    private void checkPlansForReminder(LocalDateTime targetTime, ReminderType reminderType) {
        try {
            // 해당 시간에 시작해야 하는 미완료 Plan들 조회
            List<Plan> upcomingPlans = planRepository.findPlansStartingAt(targetTime, targetTime.plusMinutes(1));
            
            for (Plan plan : upcomingPlans) {
                BannerNotificationSendRequestDto request = new BannerNotificationSendRequestDto(
                    NotificationType.PLAN,
                    plan.getId(),
                    reminderType
                );
                
                fcmTokenCommandService.sendBannerNotification(request);
                log.info("Plan 시작 알림 발송: planId={}, reminderType={}", plan.getId(), reminderType);
            }
            
        } catch (Exception e) {
            log.error("Plan 시작 알림 스케줄링 오류: reminderType={}, error={}", reminderType, e.getMessage());
        }
    }
    
    private void checkAiPlansForReminder(LocalDateTime targetTime, ReminderType reminderType) {
        try {
            // 해당 시간에 시작해야 하는 미완료 AiPlan들 조회
            List<AiPlan> upcomingAiPlans = aiPlanRepository.findAiPlansStartingAt(
                targetTime, targetTime.plusMinutes(1));
            
            for (AiPlan aiPlan : upcomingAiPlans) {
                BannerNotificationSendRequestDto request = new BannerNotificationSendRequestDto(
                    NotificationType.AI_PLAN,
                    aiPlan.getId(),
                    reminderType
                );
                
                fcmTokenCommandService.sendBannerNotification(request);
                log.info("AiPlan 시작 알림 발송: aiPlanId={}, reminderType={}", aiPlan.getId(), reminderType);
            }
            
        } catch (Exception e) {
            log.error("AiPlan 시작 알림 스케줄링 오류: reminderType={}, error={}", reminderType, e.getMessage());
        }
    }
}
