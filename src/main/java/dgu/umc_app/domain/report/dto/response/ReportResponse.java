package dgu.umc_app.domain.report.dto.response;

import lombok.Builder;

@Builder
public record ReportResponse(
//        SummaryDTO summary,
//        DelayPatternDTO delayPattern,
        EmotionAchievement emotionAchievement
//        KeywordAnalysisDTO keywordAnalysis,
//        SuggestionsDTO suggestions
) {}