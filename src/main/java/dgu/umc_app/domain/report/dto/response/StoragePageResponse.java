package dgu.umc_app.domain.report.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record StoragePageResponse(
        @Schema(description = "사용자 땅콩 수")
        int peanutCount,

        @Schema(description = "이번달 일정 달성률")
        int completionRatePercent,

        @Schema(description = "이번달 리포트 오픈 여부")
        boolean isOpenedThisMonth,

        @Schema(description = "이번달 리포트 오픈조건(땅콩 ≥ 30 && 완료율 ≥ 80%) 충족 여부")
        boolean canOpenThisMonth,

        @Schema(description = "저번달 리포트 오픈 여부")
        boolean isOpenedLastMonth,

        @Schema(description = "\"잠김\" or \"열림\"")
        String lockState,

        @Schema(description = "true면 '오픈하기' 버튼 보여주기 위함")
        boolean isOpenButtonVisible
) {
    public static StoragePageResponse from(
            int peanutCount,
            int completionRatePercent,
            boolean isOpenedThisMonth,
            boolean canOpenThisMonth,
            boolean isOpenedLastMonth,
            String lockState,
            boolean isOpenButtonVisible
    ) {
        return StoragePageResponse.builder()
                .peanutCount(peanutCount)
                .completionRatePercent(completionRatePercent)
                .isOpenedThisMonth(isOpenedThisMonth)
                .canOpenThisMonth(canOpenThisMonth)
                .isOpenedLastMonth(isOpenedLastMonth)
                .lockState(lockState)
                .isOpenButtonVisible(isOpenButtonVisible)
                .build();
    }
}
