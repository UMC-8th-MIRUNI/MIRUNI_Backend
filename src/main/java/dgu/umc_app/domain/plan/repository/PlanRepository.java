package dgu.umc_app.domain.plan.repository;

import dgu.umc_app.domain.plan.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PlanRepository extends JpaRepository<Plan, Long> {
    List<Plan> findByUserIdAndDeadlineBetween(Long userId, LocalDate start, LocalDate end); //월별 조회
    List<Plan> findByUserIdAndDeadline(Long userId, LocalDate deadline); //일자별 조회
}