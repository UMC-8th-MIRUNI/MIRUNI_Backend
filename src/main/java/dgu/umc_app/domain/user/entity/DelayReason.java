package dgu.umc_app.domain.user.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DelayReason {
    LAZY("귀찮아서"),
    TOO_BIG_TO_START("일이 너무 커 보여서(부담)"),
    DONT_KNOW_WHERE_TO_START("무엇부터 해야 할지 몰라서"),
    PERFECTIONISM("완벽하게 해내고 싶어서"),
    CANT_CONCENTRATE("집중이 안 돼서"),
    NOT_FUN("재미없거나 하기 싫어서");

    private final String description;
}
