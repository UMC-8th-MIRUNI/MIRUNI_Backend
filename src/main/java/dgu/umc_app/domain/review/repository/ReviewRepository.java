package dgu.umc_app.domain.review.repository;

import dgu.umc_app.domain.review.dto.response.ReviewCountByDateResponse;
import dgu.umc_app.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    //날짜별 회고 갯수
    @Query("""
    SELECT new dgu.umc_app.domain.review.dto.response.ReviewCountByDateResponse(
        CAST(r.createdAt AS date), COUNT(r)
    )
    FROM Review r
    WHERE r.aiPlan.plan.user.id = :userId
    GROUP BY CAST(r.createdAt AS date)
    ORDER BY CAST(r.createdAt AS date) DESC
""")
    List<ReviewCountByDateResponse> countReviewsByDate(@Param("userId") Long userId);

    //특정 날짜의 회고 목록
    @Query("""
    SELECT r
    FROM Review r
    WHERE r.aiPlan.plan.user.id = :userId
    AND DATE(r.createdAt) = :targetDate
    ORDER BY r.createdAt DESC
""")
    List<Review> findAllByUserIdAndDate(@Param("userId") Long userId, @Param("targetDate") LocalDate targetDate);

}
