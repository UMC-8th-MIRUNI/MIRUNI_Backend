package dgu.umc_app.domain.review.dto.response;

import java.time.LocalDate;

import dgu.umc_app.domain.review.entity.Review;
import lombok.Builder;

@Builder
public record ReviewListResponse(
        Long reviewId,
        String title,
        String memo,
        LocalDate createdAt
) {
    public static ReviewListResponse from(Review review) {
        return ReviewListResponse.builder()
                .reviewId(review.getId())
                .title(review.getTitle())
                .memo(review.getMemo())
                .createdAt(review.getCreatedAt().toLocalDate())
                .build();
    }
}
