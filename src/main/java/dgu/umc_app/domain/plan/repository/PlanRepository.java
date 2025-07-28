package dgu.umc_app.domain.plan.repository;

import dgu.umc_app.domain.plan.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PlanRepository extends JpaRepository<Plan, Long> {
    @Query("""
    SELECT p FROM Plan p
    WHERE p.id = :planId AND p.user.id = :userId
""")
    Optional<Plan> findByIdAndUserId(@Param("planId") Long planId, @Param("userId") Long userId);
}
