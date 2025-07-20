package dgu.umc_app.domain.plan.repository;

import dgu.umc_app.domain.plan.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PlanRepository extends JpaRepository<Plan, Long> {
    
    // 특정 시간에 시작하는 미완료 Plan들 조회
    @Query("SELECT p FROM Plan p WHERE p.startTime BETWEEN :startTime AND :endTime AND p.isDone = false")
    List<Plan> findPlansStartingAt(@Param("startTime") LocalDateTime startTime, 
                                  @Param("endTime") LocalDateTime endTime);
}