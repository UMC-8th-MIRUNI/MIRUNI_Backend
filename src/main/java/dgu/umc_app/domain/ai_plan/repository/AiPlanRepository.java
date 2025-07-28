package dgu.umc_app.domain.ai_plan.repository;

import dgu.umc_app.domain.ai_plan.entity.AiPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AiPlanRepository extends JpaRepository<AiPlan, Long> {
    @Query("""
    SELECT ap FROM AiPlan ap
    WHERE ap.id = :aiPlanId AND ap.plan.user.id = :userId
""")
    Optional<AiPlan> findByIdAndUserId(@Param("aiPlanId") Long aiPlanId, @Param("userId") Long userId);
}
