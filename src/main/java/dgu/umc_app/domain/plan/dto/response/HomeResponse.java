package dgu.umc_app.domain.plan.dto.response;

import dgu.umc_app.domain.user.entity.User;

import java.util.List;

public record HomeResponse(
        Long userId,
        String name,
        String profileImage,
        int totalCount, // 오늘 남은 할 일
        int scheduledCount, // 예정
        int pausedCount, // 중지
        int completedCount, // 완료
        int achievementRate, // 진행률
        Tasks tasks, // 오늘 일정
        List<NextTask> nextTask // 가장 가까운 일정
) {
    public static HomeResponse of(User user, int totalCount, int scheduledCount, int pausedCount, int completedCount, int achievementRate, Tasks tasks, List<NextTask> nextTask) {
        return new HomeResponse(
                user.getId(),
                user.getName(),
                user.getProfileImage().name(),
                totalCount,
                scheduledCount,
                pausedCount,
                completedCount,
                achievementRate,
                tasks,
                nextTask
        );
    }
}
