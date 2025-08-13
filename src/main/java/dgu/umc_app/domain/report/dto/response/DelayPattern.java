package dgu.umc_app.domain.report.dto.response;

import dgu.umc_app.domain.review.entity.Mood;
import lombok.Builder;

import java.util.EnumMap;
import java.util.Map;

@Builder
public record DelayPattern(
        String mostDelayedTimeBand,   // "금요일 오후 6시~8시"
        String mostFocusedTimeBand,   // "금요일 오후 6시~8시"
        Map<String,Integer> delayByCategory // {"협업/소통": 5, ...}
) {
}
