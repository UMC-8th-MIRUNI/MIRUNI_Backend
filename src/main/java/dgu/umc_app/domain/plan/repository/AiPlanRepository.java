package dgu.umc_app.domain.plan.repository;

import dgu.umc_app.domain.plan.dto.response.HomeTaskRow;
import dgu.umc_app.domain.plan.entity.AiPlan;
import dgu.umc_app.domain.plan.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AiPlanRepository extends JpaRepository<AiPlan, Long> {
    List<AiPlan> findByPlan_UserIdAndScheduledStartBetween(Long userId, LocalDateTime start, LocalDateTime end); //월별,일자별 조회

    List<AiPlan> findByPlan_UserIdAndStatus(Long userId, Status status);    //미룬 일정, 안한 일정 조회

    //    List<AiPlan> findByIsDoneFalse();
    List<AiPlan> findByStatus(Status status);

    @Query("""
    SELECT ap FROM AiPlan ap
    WHERE ap.id = :aiPlanId AND ap.plan.user.id = :userId
""")
    Optional<AiPlan> findByIdAndUserId(@Param("aiPlanId") Long aiPlanId, @Param("userId") Long userId);

    List<AiPlan> findByPlanId(Long planId); // 일정별 세부 조회

    //보관함 페이지 -> AiPlan 조회
    @Query("""
    select ap from AiPlan ap
    join ap.plan p
    where p.user.id = :userId
    and year(ap.scheduledStart) = :year
    and month(ap.scheduledStart) = :month
""")
    List<AiPlan> findByUserIdAndMonth(Long userId, int year, int month);

    @Query("""
      select new dgu.umc_app.domain.plan.dto.response.HomeTaskRow(
          a.id,
          p.id,
          p.title,
          a.description,
          a.scheduledStart,
          a.status,
          a.stoppedAt,
          r.id
      )
      from AiPlan a
      join a.plan p
      left join dgu.umc_app.domain.review.entity.Review r
             on (r.aiPlan.id = a.id) or (r.plan.id = p.id and r.aiPlan is null)
      where p.user.id = :userId
        and a.scheduledStart between :start and :end
    """)
    List<HomeTaskRow> findTodayAiPlanRows(Long userId, LocalDateTime start, LocalDateTime end);
}
