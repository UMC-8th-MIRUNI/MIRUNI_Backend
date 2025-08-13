package dgu.umc_app.domain.user.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DelayLevel {
    NEVER("거의 미루지 않는다"),
    RARELY("가끔 미룬다"),
    NORMAL("보통이다"),
    OFTEN("자주 미룬다"),
    ALWAYS("항상 미룬다");

    private final String description;
}
