package dgu.umc_app.domain.plan.repository;

import dgu.umc_app.domain.plan.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanRepository extends JpaRepository<Plan, Long> {
}