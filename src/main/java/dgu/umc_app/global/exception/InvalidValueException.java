package dgu.umc_app.global.exception;

public class InvalidValueException extends BaseException {
    public InvalidValueException() {
        super(CommonErrorCode.INVALID_REQUEST);
    }
    public InvalidValueException(ErrorCode errorCode) {
        super(errorCode);
    }
}
