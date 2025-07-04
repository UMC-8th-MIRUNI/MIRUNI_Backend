package dgu.umc_app.global.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import dgu.umc_app.global.exception.CustomErrorResponse;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonPropertyOrder({"errorCode", "message", "result"})
public class ApiResponse<T> {

    private String errorCode;
    private String message;
    private T result;

    public static <T> ApiResponse<T> success(T result) {
        return new ApiResponse<>(null, "OK", result);
    }

    //실패응답 통일시 사용
    public static <T> ApiResponse<T> fail(CustomErrorResponse customErrorResponse) {
        return new ApiResponse<>(customErrorResponse.getErrorCode(), customErrorResponse.getMessage(), null);
    }

}
