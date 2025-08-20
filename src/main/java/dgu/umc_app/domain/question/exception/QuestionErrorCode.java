package dgu.umc_app.domain.question.exception;

import dgu.umc_app.global.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum QuestionErrorCode implements ErrorCode {
    PERSONAL_INFO_NOT_AGREED(HttpStatus.BAD_REQUEST, "Question400_1", "개인정보 수집 및 이용에 동의해야 합니다."),
    PHONE_NUMBER_MISMATCH(HttpStatus.BAD_REQUEST, "Question400_2", "입력한 전화번호가 사용자 정보와 일치하지 않습니다.");

    private final HttpStatus status;
    private final String errorCode;
    private final String message;

}
