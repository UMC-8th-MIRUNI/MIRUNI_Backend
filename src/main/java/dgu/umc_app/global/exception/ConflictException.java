package dgu.umc_app.global.exception;

public class ConflictException extends BaseException {
    public ConflictException() {
        super(CommonErrorCode.CONFLICT); 
    }

    public ConflictException(ErrorCode errorCode) {
        super(errorCode);
    }
}