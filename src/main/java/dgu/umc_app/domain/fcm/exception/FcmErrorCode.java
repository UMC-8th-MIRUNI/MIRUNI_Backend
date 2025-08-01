package dgu.umc_app.domain.fcm.exception;

import dgu.umc_app.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum FcmErrorCode implements ErrorCode {


    FCM_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "FCM001", "FCM 토큰 발송 실패"),
    NOT_FOUND_FCM_TOKEN(HttpStatus.NOT_FOUND, "FCM002", "FCM 토큰을 찾을 수 없습니다."),
    ALREADY_FINISHED_TASK(HttpStatus.BAD_REQUEST, "FCM003", "이미 완료된 작업은 등록할 수 없습니다."),
    PASSED_AWAY_TIME(HttpStatus.BAD_REQUEST, "FCM004", "이미 지나가 버린 시간은 스케줄링 할 수 없습니다.");

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
