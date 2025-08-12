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
    USER_WRONG_PASSWORD(HttpStatus.UNAUTHORIZED, "USER401_3", "비밀번호가 틀립니다."),
    INVALID_SOCIAL_TOKEN(HttpStatus.BAD_REQUEST, "USER400_4", "유효하지 않은 소셜 로그인 토큰입니다."),
    SURVEY_ALREADY_COMPLETED(HttpStatus.CONFLICT, "USER409_9", "이미 설문조사를 완료한 사용자입니다."),
    SURVEY_NOT_COMPLETED(HttpStatus.BAD_REQUEST, "USER400_10", "설문조사를 완료하지 않은 사용자입니다.");
    USER_ALREADY_DELETED(HttpStatus.BAD_REQUEST, "USER400_4", "이미 탈퇴한 사용자입니다."),
    INVALID_PASSWORD_FORMAT(HttpStatus.BAD_REQUEST, "USER400_5", "비밀번호 형식이 올바르지 않습니다."),
    SAME_PASSWORD(HttpStatus.BAD_REQUEST, "USER400_6", "새 비밀번호는 현재 비밀번호와 달라야 합니다."),
    INVALID_SOCIAL_TOKEN(HttpStatus.BAD_REQUEST, "USER400_7", "유효하지 않은 소셜 로그인 토큰입니다."),
    SOCIAL_USER_PASSWORD_CHANGE(HttpStatus.BAD_REQUEST, "USER400_8", "소셜 로그인 사용자는 비밀번호 변경이 불가능합니다.");

    private final HttpStatus status;
    private final String errorCode;
    private final String message;

}
