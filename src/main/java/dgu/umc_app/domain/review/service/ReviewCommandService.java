package dgu.umc_app.domain.review.service;


import dgu.umc_app.domain.plan.entity.AiPlan;
import dgu.umc_app.domain.plan.repository.AiPlanRepository;
import dgu.umc_app.domain.plan.entity.Plan;
import dgu.umc_app.domain.plan.repository.PlanRepository;
import dgu.umc_app.domain.review.dto.request.ReviewCreateRequest;
import dgu.umc_app.domain.review.dto.request.ReviewUpdateRequest;
import dgu.umc_app.domain.review.dto.response.ReviewCreateResponse;
import dgu.umc_app.domain.review.dto.response.ReviewDetailResponse;
import dgu.umc_app.domain.review.entity.Review;
import dgu.umc_app.domain.plan.exception.PlanErrorCode;
import dgu.umc_app.domain.plan.exception.AiPlanErrorCode;
import dgu.umc_app.domain.review.repository.ReviewRepository;
import dgu.umc_app.domain.review.exception.ReviewErrorCode;
import dgu.umc_app.global.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional
public class ReviewCommandService {

    private final ReviewRepository reviewRepository;
    private final PlanRepository planRepository;
    private final AiPlanRepository aiPlanRepository;

    /**
     * 회고 저장
     */

    public ReviewCreateResponse saveReview(Long userId, ReviewCreateRequest request) {
        Plan plan = planRepository.findByIdWithUserId(request.planId(), userId)
                .orElseThrow(() -> BaseException.type(PlanErrorCode.PLAN_NOT_FOUND));

        AiPlan aiPlan = null;
        String description;

        if (request.aiPlanId() != null) {
            aiPlan = aiPlanRepository.findByIdAndUserId(request.aiPlanId(), userId)
                    .orElseThrow(() -> BaseException.type(AiPlanErrorCode.AIPLAN_NOT_FOUND));

            description = aiPlan.getDescription();  // AI 일정 설명
        } else {
            description = plan.getDescription();  // 일반 일정 설명
        }

        Review review = Review.builder()
                .aiPlan(aiPlan)
                .plan(plan)
                .title(plan.getTitle())                   // Plan.title 복사
                .description(description)     // AiPlan or Plan description 복사
                .mood(request.mood())
                .achievement((byte) request.achievement())
                .memo(request.memo())
                .build();

        Review saved = reviewRepository.save(review);
        return ReviewCreateResponse.from(saved);
    }
    /**
     * 회고 수정
     */
    public ReviewDetailResponse updateReview(Long userId, Long reviewId, ReviewUpdateRequest request) {
        Review review = reviewRepository.findByIdAndPlanUserId(reviewId, userId)
                .orElseThrow(() -> BaseException.type(ReviewErrorCode.REVIEW_NOT_FOUND));
        review.update(request.mood(), (byte) request.achievement(), request.memo());
        return ReviewDetailResponse.from(review);
    }
    /**
     * 회고 삭제
     */
    public Long deleteReview(Long userId, Long reviewId) {
        Review review = reviewRepository.findByIdAndPlanUserId(reviewId, userId)
                .orElseThrow(() -> new BaseException(ReviewErrorCode.REVIEW_NOT_FOUND));
        reviewRepository.delete(review);
        return reviewId;
    }

}
