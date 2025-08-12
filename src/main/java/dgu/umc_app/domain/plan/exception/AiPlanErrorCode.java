package dgu.umc_app.domain.plan.exception;

import dgu.umc_app.global.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AiPlanErrorCode implements ErrorCode {

    AIPLAN_NOT_FOUND(HttpStatus.NOT_FOUND, "REVIEW404_1", "해당 AI 계획이 존재하지 않습니다."),
    AI_EMPTY_RESPONSE(HttpStatus.INTERNAL_SERVER_ERROR, "AI_001", "AI 일정 분할 응답이 비어 있습니다."),
    AI_REQUEST_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "AI_002", "AI 일정 분할 요청에 실패했습니다."),

    INVALID_AIPLAN_FIELDS(HttpStatus.BAD_REQUEST, "AIPLAN_400_1", "신규 AI 일정 필수값이 누락/형식 오류입니다."),
    INVALID_TIME_RANGE(HttpStatus.BAD_REQUEST, "AIPLAN_400_2", "시작/종료 시간이 올바르지 않습니다."),
    INVALID_REQUEST_STATE(HttpStatus.BAD_REQUEST, "COMMON_400_1", "요청 상태가 올바르지 않습니다."),

    AFTER_DEADLINE(HttpStatus.BAD_REQUEST, "AIPLAN_400_3", "세부 일정이 마감기한 이후입니다.");

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
