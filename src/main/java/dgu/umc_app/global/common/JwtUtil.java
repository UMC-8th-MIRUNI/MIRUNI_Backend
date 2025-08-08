package dgu.umc_app.global.common;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import dgu.umc_app.global.authorize.CustomUserDetails;
import dgu.umc_app.global.exception.BaseException;
import dgu.umc_app.global.exception.CommonErrorCode;
import dgu.umc_app.domain.user.exception.AuthErrorCode;
import dgu.umc_app.domain.user.dto.TokenDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Date;

@Component
public class JwtUtil {
    private final Algorithm algorithm;
    
    private static final long ACCESS_TOKEN_EXPIRATION = 1000 * 60 * 60;         // 1시간
    private static final long REFRESH_TOKEN_EXPIRATION = 1000 * 60 * 60 * 24 * 7; // 7일
    private static final long TEMP_TOKEN_EXPIRATION = 1000 * 60 * 5;            // 5분

    public JwtUtil(@Value("${jwt.secret-key}") String secretKey) {
        this.algorithm = Algorithm.HMAC256(secretKey);
    }

    // Authentication 기반 토큰 생성 
    public String generateAccessToken(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String userId = userDetails.getId().toString();
        
        return JWT.create()
                .withSubject(userId)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
                .withClaim("type", "ACCESS")
                .sign(algorithm);
    }

    public String generateRefreshToken(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String userId = userDetails.getId().toString();
        
        return JWT.create()
                .withSubject(userId)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))
                .withClaim("type", "REFRESH")
                .sign(algorithm);
    }

    public String generateTempToken(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String userId = userDetails.getId().toString();
        
        return JWT.create()
                .withSubject(userId)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + TEMP_TOKEN_EXPIRATION))
                .withClaim("type", "TEMP")
                .sign(algorithm);
    }

    public Long getUserIdFromToken(String token) {
        try {
            String userIdStr = JWT.require(algorithm)
                    .build()
                    .verify(token)
                    .getSubject();
            return Long.valueOf(userIdStr);
        } catch (Exception e) {
            throw BaseException.type(CommonErrorCode.UNAUTHORIZED);
        }
    }

    public long getAccessTokenExpirationInSeconds() {
        return ACCESS_TOKEN_EXPIRATION / 1000;
    }

    public long getRefreshTokenExpirationInSeconds() {
        return REFRESH_TOKEN_EXPIRATION / 1000;
    }

    public long getRemainingTimeInSeconds(String token) {
        try {
            Date expiresAt = JWT.require(algorithm)
                    .build()
                    .verify(token)
                    .getExpiresAt();
            
            if (expiresAt == null) {
                return 0;
            }
            
            long remainingTime = expiresAt.getTime() - System.currentTimeMillis();
            return Math.max(0, remainingTime / 1000);
        } catch (Exception e) {
            return 0; 
        }
    }

    public boolean validateToken(String token) {
        try {
            JWT.require(algorithm).build().verify(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getTokenType(String token) {
        try {
            return JWT.require(algorithm)
                    .build()
                    .verify(token)
                    .getClaim("type")
                    .asString();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isTempToken(String token) {
        return "TEMP".equals(getTokenType(token));
    }

    public boolean isAccessToken(String token) {
        return "ACCESS".equals(getTokenType(token));
    }

    public boolean isRefreshToken(String token) {
        return "REFRESH".equals(getTokenType(token));
    }

    public String getCurrentToken() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw BaseException.type(CommonErrorCode.INTERNAL_SERVER_ERROR);
        }
        
        String header = attributes.getRequest().getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        throw BaseException.type(AuthErrorCode.INVALID_TOKEN);
    }

    public TokenDto createTokenDto(Authentication authentication) {
        
        String accessToken = generateAccessToken(authentication);
        String refreshToken = generateRefreshToken(authentication);
        long accessTokenExp = getAccessTokenExpirationInSeconds();
        long refreshTokenExp = getRefreshTokenExpirationInSeconds();
        
        return TokenDto.of(accessToken, refreshToken, accessTokenExp, refreshTokenExp);
    }
}