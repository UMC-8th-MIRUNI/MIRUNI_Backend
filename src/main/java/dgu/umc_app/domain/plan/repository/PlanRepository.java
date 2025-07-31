package dgu.umc_app.domain.plan.repository;

import dgu.umc_app.domain.plan.entity.Plan;
import dgu.umc_app.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;



public interface PlanRepository extends JpaRepository<Plan, Long> {
    Optional<Plan> findByIdAndUserId(Long id, Long userId);
    List<Plan> findByUserIdAndExecuteDateBetween(Long userId, LocalDateTime start, LocalDateTime end); //월별 조회
    List<Plan> findByUserIdAndExecuteDate(Long userId, LocalDateTime executeDate); //일자별 조회
    List<Plan> findByUserIdAndIsDelayedTrue(Long userId);   //미룬 일정 조회

    List<Plan> findByIsDoneFalse();
}