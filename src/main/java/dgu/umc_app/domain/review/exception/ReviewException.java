package dgu.umc_app.domain.review.exception;

import dgu.umc_app.global.exception.BaseException;
import dgu.umc_app.global.exception.ErrorCode;

public class ReviewException extends BaseException {
    public ReviewException(ErrorCode errorCode) {
        super(errorCode);
    }
}
