package dgu.umc_app.domain.user.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record SurveyResponse(
    String message,
    LocalDateTime completedAt,
    String status
) {
    public static SurveyResponse of(String message, LocalDateTime completedAt, String status) {
        return new SurveyResponse(message, completedAt, status);
    }
} 