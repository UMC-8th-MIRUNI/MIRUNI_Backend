package dgu.umc_app.domain.plan.exception;

import dgu.umc_app.global.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AiPlanErrorCode implements ErrorCode {

    AI_PLAN_NOT_FOUND(HttpStatus.NOT_FOUND, "REVIEW404_1", "해당 AI 계획이 존재하지 않습니다.");

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
