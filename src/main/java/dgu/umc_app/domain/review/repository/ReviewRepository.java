package dgu.umc_app.domain.review.repository;

import dgu.umc_app.domain.review.dto.response.ReviewCountByDateResponse;
import dgu.umc_app.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    //날짜별 회고 갯수
    @Query("""
    SELECT new dgu.umc_app.domain.review.dto.response.ReviewCountByDateResponse(
        CAST(r.createdAt AS date), COUNT(r)
    )
    FROM Review r
    WHERE r.plan.user.id = :userId
    GROUP BY CAST(r.createdAt AS date)
    ORDER BY CAST(r.createdAt AS date) DESC
""")
    List<ReviewCountByDateResponse> countReviewsByDate(@Param("userId") Long userId);

    //특정 날짜의 회고 목록
    @Query("""
    SELECT r
    FROM Review r
    WHERE r.plan.user.id = :userId
    AND DATE(r.createdAt) = :targetDate
    ORDER BY r.createdAt DESC
""")
    List<Review> findAllByUserIdAndDate(@Param("userId") Long userId, @Param("targetDate") LocalDate targetDate);


    //특정 날짜 검색으로 인한 회고 블럭 조회
     @Query("""
    SELECT new dgu.umc_app.domain.review.dto.response.ReviewCountByDateResponse(
        CAST(r.createdAt AS date), COUNT(r)
    )
    FROM Review r
    WHERE r.plan.user.id = :userId
    AND CAST(r.createdAt AS date) = :targetDate
    GROUP BY CAST(r.createdAt AS date)
    """)
    ReviewCountByDateResponse countByUserIdAndDate(@Param("userId") Long userId,
                                                   @Param("targetDate") java.sql.Date targetDate);

     //단일 회고 상세 조회
     @Query("""
    SELECT r FROM Review r
    WHERE r.id = :reviewId AND r.plan.user.id = :userId
""")
     Optional<Review> findByIdAndUserId(@Param("reviewId") Long reviewId, @Param("userId") Long userId);

}
