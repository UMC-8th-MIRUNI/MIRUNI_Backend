package dgu.umc_app.global.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Slf4j
@Configuration
@EnableScheduling
public class SchedulerConfig {

    @Bean
    @Primary
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        
        // 동시에 처리할 수 있는 알림 개수, 보통 예상 사용자 수 * 0.1% 로 설정
        scheduler.setPoolSize(10);
        
        // 스레드 이름 접두사
        scheduler.setThreadNamePrefix("notification-scheduler-");
        
        // 서버 종료시 실행중인 알림이 중간에 끊어지지 않도록
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        
        // 종료 대기 시간 (30초, 너무 길면 서버 종료 지연)
        scheduler.setAwaitTerminationSeconds(30);
        
        // 스케줄러 초기화
        scheduler.initialize();
        
        log.info("TaskScheduler 초기화 완료 - 스레드 풀 크기: {}", scheduler.getPoolSize());
        
        return scheduler;
    }
}
