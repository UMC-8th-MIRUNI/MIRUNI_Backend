package dgu.umc_app.domain.user.exception;
import dgu.umc_app.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER404_1", "해당 일정이 존재하지 않습니다.");

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
