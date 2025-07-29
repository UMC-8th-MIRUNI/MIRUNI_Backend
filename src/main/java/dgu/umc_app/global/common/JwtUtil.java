package dgu.umc_app.global.common;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import dgu.umc_app.global.authorize.CustomUserDetails;
import dgu.umc_app.global.exception.BaseException;
import dgu.umc_app.global.exception.CommonErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

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
}