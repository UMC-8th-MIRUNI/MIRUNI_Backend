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
    EXPECTED_MINUTES_REQUIRED(HttpStatus.BAD_REQUEST, "PLAN_003","예상 소요시간은 필수 값입니다.");


    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
