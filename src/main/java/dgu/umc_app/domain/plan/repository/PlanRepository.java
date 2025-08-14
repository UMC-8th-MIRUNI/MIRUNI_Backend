package dgu.umc_app.domain.plan.repository;

import dgu.umc_app.domain.plan.entity.Plan;
import dgu.umc_app.domain.plan.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import java.time.LocalDateTime;
import java.util.List;


public interface PlanRepository extends JpaRepository<Plan, Long> {
    List<Plan> findByUserIdAndScheduledStartBetween(Long userId, LocalDateTime start, LocalDateTime end); //월별,일자별 조회
    List<Plan> findByUserIdAndStatus(Long userId, Status status);   //미룬 일정, 안 한 일정 조회

    List<Plan> findByIsDoneFalse();
    List<Plan> findByStatus(Status status);
    @Query("""
    SELECT p FROM Plan p
    WHERE p.id = :planId AND p.user.id = :userId
""")
    Optional<Plan> findByIdWithUserId(@Param("planId") Long planId, @Param("userId") Long userId);

    //보관함 페이지 -> AiPlan 연결 없는 일반 Plan
    @Query("""
    select p from Plan p
    where p.user.id = :userId
    and year(p.scheduledStart) = :year
    and month(p.scheduledStart) = :month
    and p.id not in (
        select distinct ap.plan.id from AiPlan ap
    )
""")
    List<Plan> findIndependentPlans(Long userId, int year, int month);
}
