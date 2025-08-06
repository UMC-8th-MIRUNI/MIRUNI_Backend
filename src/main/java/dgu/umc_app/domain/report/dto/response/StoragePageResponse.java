package dgu.umc_app.domain.report.dto.response;

import lombok.Builder;

@Builder
public record StoragePageResponse(
        int peanutCount,    //사용자 땅콩 수
        int completionRatePercent,  //이번달 일정 달성률
        boolean isOpenedThisMonth,  //이번달 리포트 오픈 여부
        boolean canOpenThisMonth,   //이번달 리포트 오픈조건(땅콩 ≥ 30 && 완료율 ≥ 80%) 충족 여부
        boolean isOpenedLastMonth,  //저번달 리포트 오픈 여부
        String lockState,            // "잠김" or "열림"
        boolean isOpenButtonVisible  // true면 '오픈하기' 버튼 보여줌
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
