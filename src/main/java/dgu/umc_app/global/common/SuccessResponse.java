package dgu.umc_app.global.common;

import dgu.umc_app.global.response.ApiResponse;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SuccessResponse<T> {

    public static <T> ResponseEntity<ApiResponse<T>> ok(T result) {
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    public static <T> ResponseEntity<ApiResponse<T>> created(T result) {
        return ResponseEntity.status(SuccessCode.CREATED.getHttpStatus())
                .body(ApiResponse.success(result));
    }

    public static <T> ResponseEntity<ApiResponse<T>> noContent() {
        return ResponseEntity.status(SuccessCode.NO_CONTENT.getHttpStatus())
                .body(ApiResponse.success(null));
    }
} 