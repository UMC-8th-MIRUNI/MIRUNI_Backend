package dgu.umc_app.domain.user.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DelayReason {
    LAZY(1, "귀찮아서"),
    TOO_BIG_TO_START(2, "일이 너무 커 보여서(부담)"),
    DONT_KNOW_WHERE_TO_START(4, "무엇부터 해야 할지 몰라서"),
    PERFECTIONISM(8, "완벽하게 해내고 싶어서"),
    CANT_CONCENTRATE(16, "집중이 안 돼서"),
    NOT_FUN(32, "재미없거나 하기 싫어서");

    private final int value;
    private final String description;

    public static boolean isSet(long mask, DelayReason reason) {
        return (mask & reason.getValue()) != 0;
    }

    public static long addToMask(long mask, DelayReason reason) {
        return mask | reason.getValue();
    }

    public static long removeFromMask(long mask, DelayReason reason) {
        return mask & ~reason.getValue();
    }

    public static long createMask(java.util.Set<DelayReason> reasons) {
        long mask = 0;
        for (DelayReason reason : reasons) {
            mask = addToMask(mask, reason);
        }
        return mask;
    }

    public static java.util.Set<DelayReason> fromMask(long mask) {
        java.util.Set<DelayReason> reasons = new java.util.HashSet<>();
        for (DelayReason reason : values()) {
            if (isSet(mask, reason)) {
                reasons.add(reason);
            }
        }
        return reasons;
    }
}
