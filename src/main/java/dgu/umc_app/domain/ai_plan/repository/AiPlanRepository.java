package dgu.umc_app.domain.ai_plan.repository;

import dgu.umc_app.domain.ai_plan.entity.AiPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AiPlanRepository extends JpaRepository<AiPlan, Long> {

    // 특정 시간에 시작하는 미완료 AiPlan들 조회
    List<AiPlan> findByStartTimeBetweenAndIsDoneFalse(LocalDateTime startTime,
                                                      LocalDateTime endTime);
}