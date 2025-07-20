package dgu.umc_app.domain.ai_plan.repository;

import dgu.umc_app.domain.ai_plan.entity.AiPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AiPlanRepository extends JpaRepository<AiPlan, Long> {
    
    // 특정 시간에 시작하는 미완료 AiPlan들 조회
    @Query("SELECT a FROM AiPlan a WHERE a.startTime BETWEEN :startTime AND :endTime AND a.isDone = false")
    List<AiPlan> findAiPlansStartingAt(@Param("startTime") LocalDateTime startTime,
                                      @Param("endTime") LocalDateTime endTime);
}