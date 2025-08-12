package dgu.umc_app.domain.report.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record Summary(

        @Schema(description = "총 일정 수")
        int totalPlans,

        @Schema(description = "완료한 일정 수")
        int completedPlans,

        @Schema(description = "완료 비율(%)")
        int completionRatePercent,

        @Schema(description = "표시용 비율 문자열: '완료/총 (xx%)'")
        String completionRatioText
) {
    /** 완료건수/총건수로부터 퍼센트를 계산해서 생성 */
    public static Summary ofCounts(int totalPlans, int completedPlans) {
        int percent = calcPercent(completedPlans, totalPlans);
        return Summary.builder()
                .totalPlans(totalPlans)
                .completedPlans(completedPlans)
                .completionRatePercent(percent)
                .completionRatioText(format(completedPlans, totalPlans, percent))
                .build();
    }

    /** 퍼센트가 이미 계산되어 있을 때 생성 */
    public static Summary from(int totalPlans, int completedPlans, int completionRatePercent) {
        return Summary.builder()
                .totalPlans(totalPlans)
                .completedPlans(completedPlans)
                .completionRatePercent(completionRatePercent)
                .completionRatioText(format(completedPlans, totalPlans, completionRatePercent))
                .build();
    }

    private static int calcPercent(int completed, int total) {
        if (total <= 0) return 0;
        return (int) Math.round((completed * 100.0) / total);
    }

    private static String format(int completed, int total, int percent) {
        return completed + "/" + total + " (" + percent + "%)";
    }
}
