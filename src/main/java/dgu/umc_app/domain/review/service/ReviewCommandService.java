package dgu.umc_app.domain.review.service;


import dgu.umc_app.domain.plan.entity.AiPlan;
import dgu.umc_app.domain.plan.repository.AiPlanRepository;
import dgu.umc_app.domain.plan.entity.Plan;
import dgu.umc_app.domain.plan.repository.PlanRepository;
import dgu.umc_app.domain.review.dto.request.ReviewCreateRequest;
import dgu.umc_app.domain.review.dto.response.ReviewCreateResponse;
import dgu.umc_app.domain.review.entity.Review;
import dgu.umc_app.domain.review.exception.ReviewErrorCode;
import dgu.umc_app.domain.plan.exception.PlanErrorCode;
import dgu.umc_app.domain.plan.exception.AiPlanErrorCode;
import dgu.umc_app.domain.review.repository.ReviewRepository;
import dgu.umc_app.global.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class ReviewCommandService {

    private final ReviewRepository reviewRepository;
    private final PlanRepository planRepository;
    private final AiPlanRepository aiPlanRepository;

    /**
     * 회고 저장
     */
    @Transactional
    public ReviewCreateResponse saveReview(ReviewCreateRequest request) {
        AiPlan aiPlan = aiPlanRepository.findById(request.aiPlanId())
                .orElseThrow(() -> BaseException.type(AiPlanErrorCode.AI_PLAN_NOT_FOUND));

        Plan plan = planRepository.findById(request.planId())
                .orElseThrow(() -> BaseException.type(PlanErrorCode.PLAN_NOT_FOUND));

        Review review = Review.builder()
                .aiPlan(aiPlan)
                .plan(plan)
                .title(plan.getTitle())                   // Plan.title 복사
                .description(aiPlan.getDescription())     // AiPlan.description 복사
                .mood(request.mood())
                .achievement((byte) request.achievement())
                .memo(request.memo())
                .build();

        Review saved = reviewRepository.save(review);
        return ReviewCreateResponse.from(saved);
    }
}