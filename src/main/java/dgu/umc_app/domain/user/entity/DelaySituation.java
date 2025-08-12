package dgu.umc_app.domain.user.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DelaySituation {
    PHONE("휴대폰을 사용할 때 (SNS, 게임 등)"),
    VIDEO("넷플릭스, 드라마, 유튜브 등 영상 시청"),
    MEET_FRIENDS("친구, 사람들을 만날 때"),
    TOO_MUCH_WORK("다른 일이 너무 많을 때"),
    TOO_TIRED("너무 피곤할 때");

    private final String description;
}
