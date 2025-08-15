package dgu.umc_app.domain.report.dto.response;

import dgu.umc_app.domain.review.entity.Mood;
import lombok.Builder;

import java.util.EnumMap;
import java.util.Map;

@Builder
public record EmotionAchievement(
        Map<Mood,Integer> moodPercents, //감정별 비율 (HAPPY, SAD, ANGRY, RELAXED, ANXIOUS)
        int averageAchievementPercent // 평균 성취도
) {
}