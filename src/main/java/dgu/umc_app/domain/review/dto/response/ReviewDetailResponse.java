package dgu.umc_app.domain.review.dto.response;


import dgu.umc_app.domain.review.entity.Mood;
import dgu.umc_app.domain.review.entity.Review;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ReviewDetailResponse(

        @Schema(description = "회고 ID")
        Long id,

        @Schema(description = "AI 계획 ID")
        Long aiPlanId,

        @Schema(description = "일정 ID")
        Long planId,

        @Schema(description = "기분")
        Mood mood,

        @Schema(description = "회고 제목")
        String title,

        @Schema(description = "성취도")
        byte achievement,

        @Schema(description = "만족도")
        byte satisfaction,

        @Schema(description = "회고 메모")
        String memo,

        @Schema(description = "작성일시")
        LocalDateTime createdAt

) {
    public static ReviewDetailResponse from(Review review) {
        return ReviewDetailResponse.builder()
                .id(review.getId())
                .aiPlanId(review.getAiPlan().getId())
                .planId(review.getPlan().getId())
                .mood(review.getMood())
                .title(review.getTitle())
                .achievement(review.getAchievement())
                .satisfaction(review.getSatisfaction())
                .memo(review.getMemo())
                .createdAt(review.getCreatedAt())
                .build();
    }
}

