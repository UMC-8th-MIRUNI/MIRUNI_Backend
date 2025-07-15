package dgu.umc_app.domain.review.dto;

import dgu.umc_app.domain.ai_plan.entity.AiPlan;
import dgu.umc_app.domain.plan.entity.Plan;
import dgu.umc_app.domain.review.entity.Mood;
import dgu.umc_app.domain.review.entity.Review;
import jakarta.validation.constraints.*;

public record ReviewCreateRequest(

        @NotNull(message = "aiPlanId는 필수입니다.")
        Long aiPlanId,

        @NotNull(message = "planId는 필수입니다.")
        Long planId,

        @NotNull(message = "기분은 필수입니다.")
        Mood mood,

        @NotBlank @Size(max = 50 , message = "회고 제목은 50자 이내여야 합니다.")
        String title,

        @Min(value = 0, message = "성취도는 0 이상이어야 합니다.")
        @Max(value = 100, message = "성취도는 100 이하여야 합니다.")
        int achievement,

        @Min(value = 0, message = "만족도는 0 이상이어야 합니다.")
        @Max(value = 100, message = "만족도는 100 이하여야 합니다.")
        int satisfaction,

        @NotBlank(message = "회고 메모는 비워둘 수 없습니다.")
        @Size(max = 255, message = "회고 메모는 255자 이내여야 합니다.")
        String memo

) {
        public Review toEntity(AiPlan aiPlan, Plan plan) {
                return Review.builder()
                        .aiPlan(aiPlan)
                        .plan(plan)
                        .mood(mood)
                        .title(title)
                        .achievement((byte) achievement)     // byte로 변환해서 저장
                        .satisfaction((byte) satisfaction)   // byte로 변환해서 저장
                        .memo(memo)
                        .build();
        }
}
