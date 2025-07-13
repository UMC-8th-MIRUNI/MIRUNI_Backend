package dgu.umc_app.global.common;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum SuccessCode {

    /**
     * 200 Ok
     */
    OK(HttpStatus.OK, "OK"),

    /**
     * 201 Created
     */
    CREATED(HttpStatus.CREATED, "OK"),

    /**
     * 204 No Content
     */
    NO_CONTENT(HttpStatus.NO_CONTENT, "OK");

    private final HttpStatus httpStatus;
    private final String message;
} 