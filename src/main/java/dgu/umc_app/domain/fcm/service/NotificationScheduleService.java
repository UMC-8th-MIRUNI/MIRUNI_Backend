package dgu.umc_app.domain.fcm.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import dgu.umc_app.domain.fcm.entity.NotificationType;
import dgu.umc_app.domain.fcm.entity.ReminderType;
import dgu.umc_app.domain.fcm.exception.FcmErrorCode;
import dgu.umc_app.domain.plan.entity.AiPlan;
import dgu.umc_app.domain.plan.entity.Plan;
import dgu.umc_app.domain.plan.repository.AiPlanRepository;
import dgu.umc_app.domain.plan.repository.PlanRepository;
import dgu.umc_app.global.exception.BaseException;
import jakarta.transaction.Transactional;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationScheduleService {

    private final TaskScheduler taskScheduler;

    // 스케줄된 작업들을 추적하는 맵
    private final Map<String, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();
    private final FcmTokenQueryService fcmTokenQueryService;
    private final FirebaseMessaging firebaseMessaging;
    private final PlanRepository planRepository;
    private final AiPlanRepository aiPlanRepository;


    //Plan 알림 등록
    public void scheduleNotification(Plan plan){

        if(plan.isDone()){
            throw BaseException.type(FcmErrorCode.ALREADY_FINISHED_TASK);
        }

        scheduleNotificationAtTime(NotificationTask.builder()
                .userId(plan.getUser().getId())
                .title("할일 알림")
                .body(String.format("1시간 뒤에 '%s'가 예정되어 있어!", plan.getTitle()))
                .type(NotificationType.PLAN)
                .targetId(plan.getId())
                .reminderType(ReminderType.ONE_HOUR_BEFORE)
                .notificationTime(plan.getExecuteDate().minusHours(1))
                .build());

        scheduleNotificationAtTime(NotificationTask.builder()
                .userId(plan.getUser().getId())
                .title("할일 알림")
                .body(String.format("10분 뒤에 '%s'가 예정되어 있어!", plan.getTitle()))
                .type(NotificationType.PLAN)
                .targetId(plan.getId())
                .reminderType(ReminderType.TEN_MINUTES_BEFORE)
                .notificationTime(plan.getExecuteDate().minusMinutes(10))
                .build());


        log.info("Plan 알림 스케줄 등록 완료: planId = {}, startTime = {}", plan.getId(), plan.getExecuteDate());
    }

    //AIPlan 알림 등록
    public void scheduleNotification(AiPlan aiplan){

        if(aiplan.isDone()){
            throw BaseException.type(FcmErrorCode.ALREADY_FINISHED_TASK);
        }

        scheduleNotificationAtTime(NotificationTask.builder()
                .userId(aiplan.getPlan().getUser().getId())
                .title("할일 알림")
                .body(String.format("1시간 뒤에 '%s'가 예정되어 있어!", aiplan.getDescription()))
                .type(NotificationType.AI_PLAN)
                .targetId(aiplan.getId())
                .reminderType(ReminderType.ONE_HOUR_BEFORE)
                .notificationTime(aiplan.getTaskTime().minusHours(1))
                .build());


        scheduleNotificationAtTime(NotificationTask.builder()
                .userId(aiplan.getPlan().getUser().getId())
                .title("할일 알림")
                .body(String.format("10분 뒤에 '%s'가 예정되어 있어!", aiplan.getDescription()))
                .type(NotificationType.AI_PLAN)
                .targetId(aiplan.getId())
                .reminderType(ReminderType.TEN_MINUTES_BEFORE)
                .notificationTime(aiplan.getTaskTime().minusMinutes(10))
                .build());


        log.info("AiPlan 알림 스케줄 등록 완료: planId = {}, startTime = {}", aiplan.getId(), aiplan.getTaskTime());
    }

    // Plan 알림등록 취소
    public void cancelNotification(Plan plan){

        String oneHourKey = createScheduleKey(NotificationTask.builder()
                .type(NotificationType.PLAN)
                .targetId(plan.getId())
                .reminderType(ReminderType.ONE_HOUR_BEFORE)
                .build());

        cancelExistingSchedule(oneHourKey);

        String tenMinutesKey = createScheduleKey(NotificationTask.builder()
                .type(NotificationType.PLAN)
                .targetId(plan.getId())
                .reminderType(ReminderType.TEN_MINUTES_BEFORE)
                .build());

        cancelExistingSchedule(tenMinutesKey);

        log.info("알림 스케줄 취소 완료: type = {}, targetId = {}", NotificationType.PLAN, plan.getId());
    }

    // AiPlan 알림등록 취소
    public void cancelNotification(AiPlan aiplan){

        String oneHourKey = createScheduleKey(NotificationTask.builder()
                .type(NotificationType.AI_PLAN)
                .targetId(aiplan.getId())
                .reminderType(ReminderType.ONE_HOUR_BEFORE)
                .build());

        cancelExistingSchedule(oneHourKey);

        String tenMinutesKey = createScheduleKey(NotificationTask.builder()
                .type(NotificationType.AI_PLAN)
                .targetId(aiplan.getId())
                .reminderType(ReminderType.TEN_MINUTES_BEFORE)
                .build());

        cancelExistingSchedule(tenMinutesKey);

        log.info("알림 스케줄 취소 완료: type = {}, targetId = {}", NotificationType.AI_PLAN, aiplan.getId());
    }

    @Transactional
    //앱시작시 미완료 task들 재스케줄링
    @EventListener(ApplicationReadyEvent.class)
    public void rescheduleExistingPlans(){
        log.info("기존 Plan들 재스케줄링 시작");

        try{
            //미완료 Plan 재스케줄링
            List<Plan> incompletePlans = planRepository.findByIsDoneFalse();
            for (Plan plan : incompletePlans) {
                scheduleNotification(plan);
            }
            //미완료 AiPlan 재스케줄링
            List<AiPlan> incompleteAiPlans = aiPlanRepository.findByIsDoneFalse();
            for (AiPlan aiplan : incompleteAiPlans) {
                scheduleNotification(aiplan);
            }

            log.info("기존 Task들 재스케줄링 완료: Plan = {}, AiPlan = {}",
                    incompletePlans.size(), incompleteAiPlans.size());

        }catch(Exception e){
            log.error("재스케줄링 중 오류 발생: {}", e.getMessage());
        }


    }

    // == 스케줄키 생성 메서드 == //
    private String createScheduleKey(NotificationTask task){
        return String.format("%s_%d_%s", task.type().name(), task.targetId(), task.reminderType().name());
    }

    // == 알림 생성 관련 메서드 == //

    // 스케줄링 메서드 //
    private void scheduleNotificationAtTime(NotificationTask task){

        if(task.notificationTime().isBefore(LocalDateTime.now())){
            log.debug("지난 시간이므로 스케줄하지 않음: type={}, targetId={}, time={}",
                    task.type(), task.targetId(), task.notificationTime());
            throw BaseException.type(FcmErrorCode.PASSED_AWAY_TIME);
        }

        String scheduleKey = createScheduleKey(task);

        Runnable scheduledTask = () -> sendNotification(task);
        Instant executionTime = task.notificationTime().atZone(ZoneId.systemDefault()).toInstant();

        ScheduledFuture<?> future = taskScheduler.schedule(scheduledTask, executionTime);
        scheduledTasks.put(scheduleKey, future);

        log.debug("스케줄 등록: key = {}, 실행시간 = {}", scheduleKey, task.notificationTime());

    }



    // == 알림 전송 메서드(전체 과정) == /
    private void sendNotification(NotificationTask task){
        try{
            sendFcmMessage(task);
            log.info("알림전송 성공: type = {}, targetId = {}, reminderType = {}",
                    task.type(), task.targetId(), task.reminderType());
        } catch (Exception e){
            log.error("알림 전송 실패: type = {}, targetId = {}, reminderType = {}",
                    task.type(), task.targetId(), task.reminderType(), e);
        } finally {
            String scheduleKey = createScheduleKey(task);
            scheduledTasks.remove(scheduleKey);
        }

    }

    // == 메시지 생성 후 전송 메서드 == //
    private void sendFcmMessage(NotificationTask task){

        List<String> activeTokens = fcmTokenQueryService.getActiveTokenByUserId(task.userId());

        if(activeTokens.isEmpty()){
            log.debug("활성 FCM 토큰이 없음: userId={}", task.userId());
            return;
        }

        try{
            MulticastMessage message = MulticastMessage.builder()
                    .putData("type", task.type().name())
                    .putData("targetId", task.targetId().toString())
                    .putData("reminderType", task.reminderType().name())
                    .setNotification(Notification.builder()
                            .setTitle(task.title())
                            .setBody(task.body())
                            .build())
                    .addAllTokens(activeTokens)
                    .build();

            firebaseMessaging.sendEachForMulticast(message);
        } catch (FirebaseMessagingException e){
            throw BaseException.type(FcmErrorCode.FCM_SEND_FAILED);
        }

    }

    // == 알림 삭제 관련 메서드 == //
    private void cancelExistingSchedule(String scheduleKey){
        ScheduledFuture<?> existingFuture = scheduledTasks.remove(scheduleKey);
        if (existingFuture != null && !existingFuture.isDone()) {
            boolean cancelled = existingFuture.cancel(false);
            log.debug("스케줄 취소: key = {}, 성공 = {}", scheduleKey, cancelled);
        }
    }


    // == 관련 record == //
    @Builder
    private record NotificationTask(Long userId,
                                    String title,
                                    String body,
                                    NotificationType type,
                                    Long targetId,
                                    ReminderType reminderType,
                                    LocalDateTime notificationTime){}


}
