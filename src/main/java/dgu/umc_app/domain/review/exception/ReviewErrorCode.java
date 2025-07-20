package dgu.umc_app.domain.review.exception;

import dgu.umc_app.global.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ReviewErrorCode implements ErrorCode {

    AI_PLAN_NOT_FOUND(HttpStatus.NOT_FOUND, "REVIEW404_1", "해당 AI 계획이 존재하지 않습니다."),
    PLAN_NOT_FOUND(HttpStatus.NOT_FOUND, "REVIEW404_2", "해당 일반 일정이 존재하지 않습니다."),
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "REVIEW404_3", "해당 회고가 존재하지 않습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "REVIEW404_4", "해당 유저가 존재하지 않습니다.");

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
