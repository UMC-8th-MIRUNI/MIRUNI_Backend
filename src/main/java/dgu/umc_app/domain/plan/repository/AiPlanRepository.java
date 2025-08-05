package dgu.umc_app.domain.plan.repository;

import dgu.umc_app.domain.plan.entity.AiPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface AiPlanRepository extends JpaRepository<AiPlan, Long> {
    List<AiPlan> findByPlan_UserIdAndScheduledStartBetween(Long userId, LocalDateTime start, LocalDateTime end); //월별,일자별 조회
    List<AiPlan> findByPlan_UserIdAndIsDelayedTrue(Long userId);
    List<AiPlan> findByIsDoneFalse();

}