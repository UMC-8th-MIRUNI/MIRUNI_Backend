package dgu.umc_app.domain.review.service;


import dgu.umc_app.domain.ai_plan.entity.AiPlan;
import dgu.umc_app.domain.ai_plan.repository.AiPlanRepository;
import dgu.umc_app.domain.plan.entity.Plan;
import dgu.umc_app.domain.plan.repository.PlanRepository;
import dgu.umc_app.domain.review.dto.request.ReviewCreateRequest;
import dgu.umc_app.domain.review.dto.response.ReviewCreateResponse;
import dgu.umc_app.domain.review.entity.Review;
import dgu.umc_app.domain.review.exception.ReviewErrorCode;
import dgu.umc_app.domain.plan.exception.PlanErrorCode;
import dgu.umc_app.domain.ai_plan.exception.AiPlanErrorCode;
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
     * @param request 회고 작성 요청 DTO
     * @return 저장된 회고 ID
     */
    @Transactional
    public ReviewCreateResponse saveReview(ReviewCreateRequest request) {
        AiPlan aiPlan = aiPlanRepository.findById(request.aiPlanId())
                .orElseThrow(() -> BaseException.type(AiPlanErrorCode.AIPLAN_NOT_FOUND));

        Plan plan = planRepository.findById(request.planId())
                .orElseThrow(() -> BaseException.type(PlanErrorCode.PLAN_NOT_FOUND));

        Review review = request.toEntity(aiPlan, plan);
        Review saved = reviewRepository.save(review);

        return ReviewCreateResponse.from(saved);
    }


}
