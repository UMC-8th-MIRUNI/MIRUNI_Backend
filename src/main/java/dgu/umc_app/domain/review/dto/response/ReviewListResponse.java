package dgu.umc_app.domain.review.dto.response;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import dgu.umc_app.domain.review.entity.Review;
import lombok.Builder;

@Builder
public record ReviewListResponse(
        Long reviewId,
        String title,
        String description,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdAt
) {
    public static ReviewListResponse from(Review review) {
        return ReviewListResponse.builder()
                .reviewId(review.getId())
                .title(review.getTitle())
                .description(review.getDescription())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
