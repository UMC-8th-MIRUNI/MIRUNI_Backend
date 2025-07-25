package dgu.umc_app.domain.ai_plan.exception;

import dgu.umc_app.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AiPlanErrorCode implements ErrorCode {

    AIPLAN_NOT_FOUND(HttpStatus.NOT_FOUND, "AIPLAN404_1", "해당 일정이 존재하지 않습니다.");



    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
