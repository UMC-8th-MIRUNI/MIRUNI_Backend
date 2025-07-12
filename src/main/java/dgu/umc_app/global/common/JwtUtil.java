package dgu.umc_app.global.common;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {
    private final Algorithm algorithm;

    public JwtUtil(@Value("${jwt.secret-key}") String secretKey) {
        this.algorithm = Algorithm.HMAC256(secretKey);
    }

    public String generateAccessToken(String email) {
        return JWT.create()
                .withSubject(email)  
                .withIssuedAt(new Date())  
                .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 60))  
                .sign(algorithm);  
    }

    public String generateRefreshToken(String email) {
        return JWT.create()
                .withSubject(email)  
                .withIssuedAt(new Date()) 
                .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7))  
                .sign(algorithm); 
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