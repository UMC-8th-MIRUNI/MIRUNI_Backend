package dgu.umc_app.global.authorize;

import com.fasterxml.jackson.databind.ObjectMapper;
import dgu.umc_app.global.exception.CommonErrorCode;
import dgu.umc_app.global.exception.CustomErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper;

    /**
     * 인증 실패 시 호출되는 메서드
     * - HTTP 상태 코드 401(UNAUTHORIZED), CustomErrorResponse 형식으로 응답 반환
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");
        response.setStatus(CommonErrorCode.UNAUTHORIZED.getStatus().value());
        response.getWriter().write(objectMapper.writeValueAsString(CustomErrorResponse.from(CommonErrorCode.UNAUTHORIZED)));
    }
}
