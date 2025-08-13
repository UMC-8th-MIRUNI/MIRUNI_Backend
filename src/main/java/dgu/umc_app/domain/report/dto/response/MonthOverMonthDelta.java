package dgu.umc_app.domain.report.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record MonthOverMonthDelta(
        @Schema(description = "이번달 - 저번달: 총 일정수 변화")
        int deltaTotalPlans,

        @Schema(description = "이번달 - 저번달: 완료 비율(%) 변화")
        int deltaCompletionRatePercent,

        @Schema(description = "이번달 - 저번달: 총 수행시간 변화")
        int deltaTotalExecuteTime,

        @Schema(description = "이번달 - 저번달: 총 미룬시간 변화")
        int deltaTotalDelayTime
) {
    public static MonthOverMonthDelta zero() {
        return MonthOverMonthDelta.builder()
                .deltaTotalPlans(0)
                .deltaCompletionRatePercent(0)
                .deltaTotalExecuteTime(0)
                .deltaTotalDelayTime(0)
                .build();
    }

    /** current - previous 계산 */
    public static MonthOverMonthDelta of(Summary current, Summary previous) {
        return MonthOverMonthDelta.builder()
                .deltaTotalPlans(current.totalPlans() - previous.totalPlans())
                .deltaCompletionRatePercent(current.completionRatePercent() - previous.completionRatePercent())
                .deltaTotalExecuteTime(
                        getExec(current) - getExec(previous)
                )
                .deltaTotalDelayTime(
                        getDelay(current) - getDelay(previous)
                )
                .build();
    }

    private static int getExec(Summary s) {
        try { return (int) Summary.class.getMethod("totalExecuteTime").invoke(s); }
        catch (Exception ignore) {}
        return 0;
    }
    private static int getDelay(Summary s) {
        try { return (int) Summary.class.getMethod("totalDelayTime").invoke(s); }
        catch (Exception ignore) {}
        return 0;
    }
}
