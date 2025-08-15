package dgu.umc_app.global.authorize;

import com.fasterxml.jackson.databind.ObjectMapper;
import dgu.umc_app.global.exception.CommonErrorCode;
import dgu.umc_app.global.exception.CustomErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    /**
     * 인가(권한) 실패 시 호출되는 메서드
     * - HTTP 상태 코드 403(FORBIDDEN), CustomErrorResponse 형식으로 응답 반환
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");
        response.setStatus(CommonErrorCode.FORBIDDEN.getStatus().value());
        response.getWriter().write(objectMapper.writeValueAsString(CustomErrorResponse.from(CommonErrorCode.FORBIDDEN)));
    }
}
