package dgu.umc_app.domain.review.service;

import dgu.umc_app.domain.review.dto.response.ReviewDetailResponse;
import dgu.umc_app.domain.review.dto.response.ReviewListResponse;
import dgu.umc_app.domain.review.entity.Review;
import dgu.umc_app.domain.review.exception.ReviewErrorCode;
import dgu.umc_app.domain.user.exception.UserErrorCode;
import dgu.umc_app.domain.review.repository.ReviewRepository;
import dgu.umc_app.domain.user.repository.UserRepository;
import dgu.umc_app.global.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewQueryService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    /**
     * 단일 회고 상세 조회
     */
    public ReviewDetailResponse getReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> BaseException.type(ReviewErrorCode.REVIEW_NOT_FOUND));
        return ReviewDetailResponse.from(review);
    }

    /**
     * 전체 회고 목록 조회 (최신순 정렬)
     */
    public List<ReviewListResponse> getReviewListByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw BaseException.type(UserErrorCode.USER_NOT_FOUND);
        }

        List<Review> reviews = reviewRepository.findAllByUserIdOrderByCreatedAtDesc(userId);
        return reviews.stream()
                .map(ReviewListResponse::from)
                .toList();
    }
}

