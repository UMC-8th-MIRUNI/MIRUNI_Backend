package dgu.umc_app.domain.plan.dto.response;

import java.time.format.DateTimeFormatter;
import java.util.List;

public record Tasks(
        List<NotStartedItem> notStarted,
        List<PausedItem>     paused,
        List<FinishedItem>   finished
) {
    public record NotStartedItem(Long planId, Long aiPlanId, String category, String title, String scheduledStart, String status) {
        public static NotStartedItem from(HomeTaskRow r, DateTimeFormatter timeFormatter) {
            String category = (r.aiPlanId() == null) ? "BASIC" : "AI";
            return new NotStartedItem(
                    r.planId(),
                    r.aiPlanId(),
                    category,
                    r.planTitle(),
                    r.scheduledStart().format(timeFormatter),
                    "NOT_STARTED"
            );
        }
    }

    public record PausedItem(Long planId, Long aiPlanId, String category, String title, String pausedAt, String stoppedAt, String status) {
        public static PausedItem from(HomeTaskRow r, DateTimeFormatter timeFormatter) {
            String category = (r.aiPlanId() == null) ? "BASIC" : "AI";
            // TODO 중단 시간 계산 로직
            String pausedAt = "--:--";
            return new PausedItem(
                    r.planId(),
                    r.aiPlanId(),
                    category,
                    r.planTitle(),
                    pausedAt,
                    (r.stoppedAt() != null ? r.stoppedAt().format(timeFormatter) : null),
                    "PAUSED"
            );
        }
    }

    public record FinishedItem(Long planId, Long aiPlanId, String category, String title, String stoppedAt, String status, Long reviewId) {
        public static FinishedItem from(HomeTaskRow r, DateTimeFormatter timeFormatter) {
            String category = (r.aiPlanId() == null) ? "BASIC" : "AI";
            return new FinishedItem(
                    r.planId(),
                    r.aiPlanId(),
                    category,
                    r.planTitle(),
                    (r.stoppedAt() != null ? r.stoppedAt().format(timeFormatter) : null),
                    "FINISHED",
                    r.reviewId()
            );
        }
    }
}
