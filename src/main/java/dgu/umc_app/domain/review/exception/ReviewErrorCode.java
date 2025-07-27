package dgu.umc_app.domain.review.exception;

import dgu.umc_app.global.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ReviewErrorCode implements ErrorCode {

    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "REVIEW404_3", "해당 회고가 존재하지 않습니다.");

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
