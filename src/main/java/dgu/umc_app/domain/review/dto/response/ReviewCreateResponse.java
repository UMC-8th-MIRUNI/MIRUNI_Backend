package dgu.umc_app.domain.review.dto.response;

import dgu.umc_app.domain.review.entity.Mood;
import dgu.umc_app.domain.review.entity.Review;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ReviewCreateResponse(

        @Schema(description = "회고 ID")
        Long id,

        @Schema(description = "AI 계획 ID")
        Long aiPlanId,

        @Schema(description = "일정 ID")
        Long planId,

        @Schema(description = "회고 제목")
        String title,

        @Schema(description = "회고 부제목")
        String description,

        @Schema(description = "기분")
        Mood mood,

        @Schema(description = "성취도")
        byte achievement,

        @Schema(description = "메모")
        String memo,

        @Schema(description = "생성일시")
        LocalDateTime createdAt
) {
    public static ReviewCreateResponse from(Review review) {
        return ReviewCreateResponse.builder()
                .id(review.getId())
                .aiPlanId(review.getAiPlan().getId())
                .planId(review.getPlan().getId())
                .title(review.getTitle())
                .description(review.getDescription())
                .mood(review.getMood())
                .achievement(review.getAchievement())
                .memo(review.getMemo())
                .createdAt(review.getCreatedAt())
                .build();
    }

}
