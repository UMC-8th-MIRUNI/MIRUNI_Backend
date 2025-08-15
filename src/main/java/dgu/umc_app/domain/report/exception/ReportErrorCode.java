package dgu.umc_app.domain.report.exception;

import dgu.umc_app.global.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ReportErrorCode implements ErrorCode {
    LAST_MONTH_REPORT_NOT_FOUND(HttpStatus.NOT_FOUND, "REPORT404_1", "저번달 리포트가 없습니다."),
    LAST_MONTH_REPORT_NOT_OPEN(HttpStatus.NOT_FOUND, "REPORT404_2", "저번달 리포트를 오픈하지 않았습니다"),
    OPEN_CONDITION_NOT_MET(HttpStatus.BAD_REQUEST, "REPORT400_1", "오픈 조건(완료율≥80, 땅콩≥30) 미충족"),
    INSUFFICIENT_PEANUT(HttpStatus.BAD_REQUEST, "REPORT400_2", "땅콩이 부족합니다.");

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}