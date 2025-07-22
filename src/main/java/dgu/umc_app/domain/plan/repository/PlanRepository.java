package dgu.umc_app.domain.plan.repository;

import dgu.umc_app.domain.plan.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PlanRepository extends JpaRepository<Plan, Long> {

    // JPA 메서드명으로 변환
    List<Plan> findByStartTimeBetweenAndIsDoneFalse(LocalDateTime startTime,
                                                    LocalDateTime endTime);
}