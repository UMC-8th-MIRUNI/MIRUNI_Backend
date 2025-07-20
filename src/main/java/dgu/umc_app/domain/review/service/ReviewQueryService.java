package dgu.umc_app.domain.review.service;

import dgu.umc_app.domain.review.dto.response.ReviewDetailResponse;
import dgu.umc_app.domain.review.entity.Review;
import dgu.umc_app.domain.review.exception.ReviewErrorCode;
import dgu.umc_app.domain.review.repository.ReviewRepository;
import dgu.umc_app.global.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewQueryService {

    private final ReviewRepository reviewRepository;

    @Transactional(readOnly = true)
    public ReviewDetailResponse getReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> BaseException.type(ReviewErrorCode.REVIEW_NOT_FOUND));
        return ReviewDetailResponse.from(review);
    }
}

