package dgu.umc_app.domain.review.repository;

import dgu.umc_app.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
