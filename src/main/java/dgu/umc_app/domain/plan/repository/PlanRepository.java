package dgu.umc_app.domain.plan.repository;

import dgu.umc_app.domain.plan.dto.response.HomeTaskRow;
import dgu.umc_app.domain.plan.entity.Plan;
import dgu.umc_app.domain.plan.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    @Query("""
      select new dgu.umc_app.domain.plan.dto.response.HomeTaskRow(
          null,
          p.id,
          p.title,
          p.description,
          p.scheduledStart,
          p.status,
          p.stoppedAt,
          r.id
      )
      from Plan p
      left join dgu.umc_app.domain.review.entity.Review r
             on r.plan.id = p.id and r.aiPlan is null
      where p.user.id = :userId
        and p.scheduledStart between :start and :end
        and not exists (select 1 from AiPlan a where a.plan.id = p.id)
    """)
    List<HomeTaskRow> findTodayStandalonePlanRows(Long userId, LocalDateTime start, LocalDateTime end);
}
