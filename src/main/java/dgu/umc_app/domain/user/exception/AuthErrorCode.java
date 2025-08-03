package dgu.umc_app.domain.user.exception;

import dgu.umc_app.global.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthErrorCode implements ErrorCode {
    
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH401_1", "유효하지 않은 토큰입니다."),
    ALREADY_COMPLETED_SIGNUP(HttpStatus.BAD_REQUEST, "AUTH400_2", "이미 회원가입이 완료된 사용자입니다.");

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
} 