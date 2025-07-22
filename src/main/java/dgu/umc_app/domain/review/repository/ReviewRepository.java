package dgu.umc_app.domain.review.repository;

import dgu.umc_app.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query("""
    select r
    from Review r
    where r.aiPlan.plan.user.id = :userId
    order by r.createdAt desc
""")
    List<Review> findAllByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);

}
