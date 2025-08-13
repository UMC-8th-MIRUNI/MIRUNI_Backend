package dgu.umc_app.domain.report.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record ReportResponse(
        Summary summary,
        DelayPattern delayPattern,
        EmotionAchievement emotionAchievement,
        List<String> simpleKeywords,
        List<String> selfReflections,
        MonthOverMonthDelta monthOverMonthDelta
) {}