package dgu.umc_app.domain.user.exception;

import dgu.umc_app.global.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserErrorCode implements ErrorCode {
    
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER404_1", "해당 사용자가 존재하지 않습니다."),
    USER_EMAIL_EXIST(HttpStatus.CONFLICT, "USER409_2", "해당 이메일이 존재합니다."),
    USER_WRONG_PASSWORD(HttpStatus.UNAUTHORIZED, "USER401_3", "비밀번호가 틀립니다.");

    private final HttpStatus status;
    private final String errorCode;
    private final String message;

}
