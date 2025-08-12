package dgu.umc_app.domain.report.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record ReportResponse(
        Summary summary,
//        DelayPatternDTO delayPattern,
        EmotionAchievement emotionAchievement,
        List<String> simpleKeywords
        //SimplePatternSummary patternSummary
        //KeywordAnalysis keywordAnalysis
//        SuggestionsDTO suggestions
) {}