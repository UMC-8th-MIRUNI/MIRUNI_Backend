package dgu.umc_app.domain.plan.repository;

import dgu.umc_app.domain.plan.entity.AiPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AiPlanRepository extends JpaRepository<AiPlan, Long> {
    List<AiPlan> findByPlan_UserIdAndScheduledDateBetween(Long userId, LocalDate start, LocalDate end); //월별 조회
    List<AiPlan> findByPlan_UserIdAndScheduledDate(Long userId, LocalDate scheduledDate); //일자별 조회
    List<AiPlan> findByPlan_UserIdAndIsDelayedTrue(Long userId);

}