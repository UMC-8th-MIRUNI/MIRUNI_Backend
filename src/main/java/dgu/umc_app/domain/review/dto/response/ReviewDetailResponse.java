package dgu.umc_app.domain.review.dto.response;


import com.fasterxml.jackson.annotation.JsonFormat;
import dgu.umc_app.domain.review.entity.Mood;
import dgu.umc_app.domain.review.entity.Review;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDate;
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

        @Schema(description = "회고 부제목")
        String description,

        @Schema(description = "성취도")
        byte achievement,

        @Schema(description = "회고 메모")
        String memo,

        @Schema(description = "작성일시")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdAt

) {
    public static ReviewDetailResponse from(Review review) {
        return ReviewDetailResponse.builder()
                .id(review.getId())
                .aiPlanId(review.getAiPlan() != null ? review.getAiPlan().getId() : null)
                .planId(review.getPlan().getId())
                .mood(review.getMood())
                .title(review.getTitle())
                .description(review.getDescription())
                .achievement(review.getAchievement())
                .memo(review.getMemo())
                .createdAt(review.getCreatedAt())
                .build();
    }
}

