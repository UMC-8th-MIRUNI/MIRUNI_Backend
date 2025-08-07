package dgu.umc_app.domain.plan.repository;

import dgu.umc_app.domain.plan.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import java.time.LocalDateTime;
import java.util.List;


public interface PlanRepository extends JpaRepository<Plan, Long> {
    Optional<Plan> findByIdAndUserId(Long id, Long userId);
    List<Plan> findByUserIdAndScheduledStartBetween(Long userId, LocalDateTime start, LocalDateTime end); //월별,일자별 조회
    List<Plan> findByUserIdAndIsDelayedTrue(Long userId);   //미룬 일정 조회
    boolean existsByIdAndTempTimeIsNotNull(Long planId);
    List<Plan> findByIsDoneFalse();
}
