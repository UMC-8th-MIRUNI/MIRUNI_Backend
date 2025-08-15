package dgu.umc_app.domain.plan.exception;

import dgu.umc_app.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PlanErrorCode implements ErrorCode {

    PLAN_NOT_FOUND(HttpStatus.NOT_FOUND, "PLAN404_1", "해당 일정이 존재하지 않습니다."),
    INVALID_CATEGORY(HttpStatus.NOT_FOUND, "PLAN404_2", "해당 카테고리가 존재하지 않습니다."),
    INVALID_DATE_RANGE(HttpStatus.BAD_REQUEST, "PLAN400_1", "시작일은 종료일보다 이전이어야 합니다."),
    CANNOT_DELETE_BASE_PLAN(HttpStatus.BAD_REQUEST, "PLAN400_2", "기존 일정 삭제를 방지합니다."),
    PAST_DEADLINE_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "PLAN400_3", "마감 기한은 오늘 이전일 수 없습니다."),
    PAST_START_NOT_ALLOWED   (HttpStatus.BAD_REQUEST, "PLAN400_4", "시작 시간은 오늘 이전일 수 없습니다."),
    PAST_END_NOT_ALLOWED     (HttpStatus.BAD_REQUEST, "PLAN400_5", "종료 시간은 오늘 이전일 수 없습니다.");
    INVALID_EXECUTE_MINUTES(HttpStatus.BAD_REQUEST, "PLAN400_2", "수행시간 입력값이 음수입니다."),
    ACTUAL_START_REQUIRED(HttpStatus.BAD_REQUEST, "PLAN400_3", "시작시간은 필수값입니다.");

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
