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
    SURVEY_NOT_COMPLETED(HttpStatus.BAD_REQUEST, "USER400_10", "설문조사를 완료하지 않은 사용자입니다."),
    USER_ALREADY_DELETED(HttpStatus.BAD_REQUEST, "USER400_4", "이미 탈퇴한 사용자입니다."),
    INVALID_PASSWORD_FORMAT(HttpStatus.BAD_REQUEST, "USER400_5", "비밀번호 형식이 올바르지 않습니다."),
    SAME_PASSWORD(HttpStatus.BAD_REQUEST, "USER400_6", "새 비밀번호는 현재 비밀번호와 달라야 합니다."),
    SOCIAL_USER_PASSWORD_CHANGE(HttpStatus.BAD_REQUEST, "USER400_7", "소셜 로그인 사용자는 비밀번호 변경이 불가능합니다."),
    USER_EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "USER404_8", "해당 이메일로 가입된 사용자가 존재하지 않습니다."),
    INVALID_VERIFICATION_CODE(HttpStatus.BAD_REQUEST, "USER400_9", "유효하지 않은 인증 코드입니다."),
    VERIFICATION_CODE_EXPIRED(HttpStatus.BAD_REQUEST, "USER400_10", "인증 코드가 만료되었습니다."),
    VERIFICATION_CODE_ALREADY_USED(HttpStatus.BAD_REQUEST, "USER400_11", "이미 사용된 인증 코드입니다."),
    PASSWORDS_DO_NOT_MATCH(HttpStatus.BAD_REQUEST, "USER400_12", "비밀번호와 비밀번호 확인이 일치하지 않습니다."),
    EMAIL_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "USER500_13", "이메일 전송에 실패했습니다."),
    RESET_TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "USER400_14", "토큰이 만료되었습니다.");

    private final HttpStatus status;
    private final String errorCode;
    private final String message;

}
