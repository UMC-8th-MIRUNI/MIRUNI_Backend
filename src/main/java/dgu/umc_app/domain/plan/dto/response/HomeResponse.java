package dgu.umc_app.domain.plan.dto.response;

import dgu.umc_app.domain.plan.entity.Plan;
import dgu.umc_app.domain.user.entity.User;

import java.util.List;

public record HomeResponse(
        Long userId,
        String name,
        String profileImage,
        int totalCount,
        int scheduledCount,
        int pausedCount,
        int completedCount,
        double achievementRate,
        List<TaskInfo> tasks
        // TODO 중지일 경우 시간 추가
) {
        public static HomeResponse of(User user, int totalCount, int scheduledCount, int pausedCount, int completedCount, double achievementRate, List<TaskInfo> tasks) {
                return new HomeResponse(
                        user.getId(),
                        user.getName(),
                        user.getProfileImage().name(),
                        totalCount,
                        scheduledCount,
                        pausedCount,
                        completedCount,
                        achievementRate,
                        tasks
                );
        }

        public record TaskInfo(Long planId, String title, String startTime, boolean isDone) {
                public static TaskInfo from(Plan plan) {
                        return new TaskInfo(
                                plan.getId(),
                                plan.getTitle(),
                                plan.getScheduledStart().toLocalTime().toString(),
                                plan.isDone()
                        );
                }
        }
}