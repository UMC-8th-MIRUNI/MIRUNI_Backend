package dgu.umc_app.domain.plan.exception;

import dgu.umc_app.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PlanErrorCode implements ErrorCode {

    INVALID_DATE_RANGE(HttpStatus.BAD_REQUEST, "PLAN400_1", "시작일은 종료일보다 이전이어야 합니다."),
    PLAN_NOT_FOUND(HttpStatus.NOT_FOUND, "PLAN404_1", "해당 일정이 존재하지 않습니다.");

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
