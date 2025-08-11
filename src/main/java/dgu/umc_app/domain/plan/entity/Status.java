package dgu.umc_app.domain.plan.entity;

public enum Status {
    NOT_STARTED, // 미완료(실제 실행 X)
    IN_PROGRESS, // 진행중
    PAUSED,  // 중지(미루기)
    FINISHED // 완료
    }
