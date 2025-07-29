package dgu.umc_app.domain.review.service;

import dgu.umc_app.domain.review.dto.response.ReviewDetailResponse;
import dgu.umc_app.domain.review.dto.response.ReviewCountByDateResponse;
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

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewQueryService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    /**
     * 회고 목록(블럭별)조회 (내림차순)
     */
    public List<ReviewCountByDateResponse> getReviewCountByDate(Long userId) {
        return reviewRepository.countReviewsByDate(userId);
    }

    /**
     * 단일 회고 상세 조회
     */
    public ReviewDetailResponse getReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> BaseException.type(ReviewErrorCode.REVIEW_NOT_FOUND));
        return ReviewDetailResponse.from(review);
    }

    /**
     * 특정 날짜 회고 목록 조회 (최신순 정렬)
     */
    public List<ReviewListResponse> getReviewListByUserIdAndDate(Long userId, LocalDate date) {
        if (!userRepository.existsById(userId)) {
            throw BaseException.type(UserErrorCode.USER_NOT_FOUND);
        }

        List<Review> reviews = reviewRepository.findAllByUserIdAndDate(userId, date);
        if (reviews.isEmpty()) {
            throw BaseException.type(ReviewErrorCode.REVIEW_NOT_FOUND_BY_DATE);
        }
        return reviews.stream()
                .map(ReviewListResponse::from)
                .toList();
    }

}

