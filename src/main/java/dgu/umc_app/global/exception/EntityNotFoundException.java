package dgu.umc_app.global.exception;

public class EntityNotFoundException extends BaseException {
    public EntityNotFoundException() {
        super(CommonErrorCode.ENTITY_NOT_FOUND);
    }
    public EntityNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}