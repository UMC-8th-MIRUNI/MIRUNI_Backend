package dgu.umc_app.global.common;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {
    private final Algorithm algorithm;
    private static final long ACCESS_TOKEN_EXPIRATION = 1000 * 60 * 60;         // 1시간
    private static final long REFRESH_TOKEN_EXPIRATION = 1000 * 60 * 60 * 24 * 7; // 7일

    public JwtUtil(@Value("${jwt.secret-key}") String secretKey) {
        this.algorithm = Algorithm.HMAC256(secretKey);
    }

    public String generateAccessToken(String email) {
        return JWT.create()
                .withSubject(email)  
                .withIssuedAt(new Date())  
                .withExpiresAt(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))  
                .sign(algorithm);  
    }

    public long getAccessTokenExpirationInSeconds() {
        return ACCESS_TOKEN_EXPIRATION / 1000;
    }

    public String generateRefreshToken(String email) {
        return JWT.create()
                .withSubject(email)  
                .withIssuedAt(new Date()) 
                .withExpiresAt(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))  
                .sign(algorithm); 
    }

    public long getRefreshTokenExpirationInSeconds() {
        return REFRESH_TOKEN_EXPIRATION / 1000;
    }


    public String getEmail(String token) {
        return JWT.require(algorithm)
                .build()
                .verify(token)   
                .getSubject();   
    }

    public boolean validateToken(String token) {
        try {
            JWT.require(algorithm).build().verify(token); 
            return true;
        } catch (Exception e) {
            return false; 
        }
    }
}