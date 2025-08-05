package dgu.umc_app.global.config;

import dgu.umc_app.global.common.JwtUtil;
import dgu.umc_app.global.authorize.CustomUserDetailService;
import dgu.umc_app.global.service.TokenBlacklistService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final CustomUserDetailService customUserDetailService;
    private final TokenBlacklistService tokenBlacklistService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            
            try {
                if (jwtUtil.validateToken(token)) {
                    
                    // 블랙리스트 체크
                    if (tokenBlacklistService.isBlacklisted(token)) {
                        log.warn("블랙리스트된 토큰 사용 시도: {}", token);
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        return;
                    }
                    
                    Long userId = jwtUtil.getUserIdFromToken(token);
                    
                    // 임시 토큰인지 확인
                    String tokenType = jwtUtil.getTokenType(token);
                    if ("TEMP".equals(tokenType)) {
                        log.debug("임시 토큰 사용: userId={}", userId);
                    }
                    
                    UserDetails userDetails = customUserDetailService.loadUserById(userId);
                    
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());
                    
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.debug("JWT 토큰 검증 성공: userId={}", userId);
                }
            } catch (Exception e) {
                log.warn("JWT 토큰 검증 실패: {}", e.getMessage());
            }
        }
        
        filterChain.doFilter(request, response);
    }
} 