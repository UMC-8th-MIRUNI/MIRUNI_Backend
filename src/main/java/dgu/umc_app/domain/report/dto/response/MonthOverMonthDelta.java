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
        int deltaTotalDelayTime,

        @Schema(description = "저번달 대비 이번달 완료 비율 변화 문자열", nullable = true)
        String completionRateMessage
) {
    public static MonthOverMonthDelta zero() {
        return MonthOverMonthDelta.builder()
                .deltaTotalPlans(0)
                .deltaCompletionRatePercent(0)
                .deltaTotalExecuteTime(0)
                .deltaTotalDelayTime(0)
                .completionRateMessage("비교할 데이터가 없어요.")
                .build();
    }

    /** current - previous 계산 */
    public static MonthOverMonthDelta of(Summary current, Summary previous) {
        if (previous == null) {
            return MonthOverMonthDelta.builder()
                    .deltaTotalPlans(0)
                    .deltaCompletionRatePercent(0)
                    .deltaTotalExecuteTime(0)
                    .deltaTotalDelayTime(0)
                    .completionRateMessage("저번달 데이터가 없어 비교할 수 없어요.")
                    .build();
        }
        int diff = current.completionRatePercent() - previous.completionRatePercent();
        return MonthOverMonthDelta.builder()
                .deltaTotalPlans(current.totalPlans() - previous.totalPlans())
                .deltaCompletionRatePercent(diff)
                .deltaTotalExecuteTime(getExec(current) - getExec(previous))
                .deltaTotalDelayTime(getDelay(current) - getDelay(previous))
                .completionRateMessage(buildCompletionRateMessage(diff))
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

    private static String buildCompletionRateMessage(int diff) {
        if (diff > 0) return String.format("이번달은 저번달에 비해 완료비율이 %d%% 상승했어요!", diff);
        if (diff < 0) return String.format("이번달은 저번달에 비해 완료비율이 %d%% 떨어졌어요!", Math.abs(diff));
        return "이번달과 저번달의 완료비율이 동일해요!";
    }
}
