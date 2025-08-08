package dgu.umc_app.domain.plan.repository;

import dgu.umc_app.domain.plan.entity.AiPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AiPlanRepository extends JpaRepository<AiPlan, Long> {
    List<AiPlan> findByPlan_UserIdAndScheduledStartBetween(Long userId, LocalDateTime start, LocalDateTime end); //월별,일자별 조회
    List<AiPlan> findByPlan_UserIdAndIsDelayedTrue(Long userId);
    List<AiPlan> findByIsDoneFalse();

    @Query("""
    SELECT ap FROM AiPlan ap
    WHERE ap.id = :aiPlanId AND ap.plan.user.id = :userId
""")
    Optional<AiPlan> findByIdAndUserId(@Param("aiPlanId") Long aiPlanId, @Param("userId") Long userId);

    List<AiPlan> findByPlanId(Long planId); // 일정별 세부 조회

}