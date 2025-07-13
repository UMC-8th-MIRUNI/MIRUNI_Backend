package dgu.umc_app.global.common;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonPropertyOrder({"errorCode", "message", "result"})
public class SuccessResponse<T> {

    private String errorCode;
    private String message;
    private T result;

    public static <T> ResponseEntity<SuccessResponse<?>> ok(T data) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of(SuccessCode.OK, data));
    }

    public static <T> ResponseEntity<SuccessResponse<?>> created(T data) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SuccessResponse.of(SuccessCode.CREATED, data));
    }

    public static ResponseEntity<SuccessResponse<?>> noContent() {
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(SuccessResponse.of(SuccessCode.NO_CONTENT, null));
    }

    public static <T> SuccessResponse<?> of(SuccessCode successCode, T data) {
        return SuccessResponse.builder()
                .errorCode(null)
                .message(successCode.getMessage())
                .result(data)
                .build();
    }
} 