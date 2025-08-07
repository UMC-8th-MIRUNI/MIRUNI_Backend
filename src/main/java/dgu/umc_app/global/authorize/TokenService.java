package dgu.umc_app.global.authorize;

import dgu.umc_app.global.common.JwtUtil;
import dgu.umc_app.domain.user.entity.User;
import dgu.umc_app.domain.user.dto.response.UserResponse;
import dgu.umc_app.domain.user.dto.response.AuthLoginResponse;
import dgu.umc_app.domain.user.dto.response.ReissueTokenResponse;
import dgu.umc_app.domain.user.repository.UserRepository;
import dgu.umc_app.global.exception.BaseException;
import dgu.umc_app.domain.user.exception.AuthErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.data.redis.core.RedisTemplate;
import java.time.Duration;
import dgu.umc_app.domain.user.dto.TokenDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {
    
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;
    private final UserRepository userRepository;
    
    private static final String BLACKLIST_PREFIX = "blacklist:";
    private static final String REFRESH_TOKEN_PREFIX = "refresh_token:";

    // 공통 토큰 발급/저장 메서드
    private TokenDto createAndStoreTokens(User user) {
      
        Authentication authentication = createAuthentication(user);
        String accessToken = jwtUtil.generateAccessToken(authentication);
        String refreshToken = jwtUtil.generateRefreshToken(authentication);
        long accessTokenExp = jwtUtil.getAccessTokenExpirationInSeconds();
        long refreshTokenExp = jwtUtil.getRefreshTokenExpirationInSeconds();
        saveRefreshToken(user.getId().toString(), refreshToken, refreshTokenExp);
        
        return TokenDto.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .accessTokenExp(accessTokenExp)
            .refreshTokenExp(refreshTokenExp)
            .build();
    }

    public UserResponse issueTokenResponse(User user) {
        TokenDto token = createAndStoreTokens(user);
        return UserResponse.of(token.getAccessToken(), token.getRefreshToken(), token.getAccessTokenExp(), token.getRefreshTokenExp());
    }

    public AuthLoginResponse generateLoginTokens(User user, boolean isNewUser) {
        TokenDto token = createAndStoreTokens(user);
        return AuthLoginResponse.login(token.getAccessToken(), token.getRefreshToken(), token.getAccessTokenExp(), token.getRefreshTokenExp(), isNewUser);
    }

    public String generateTempTokenForUser(User user) {
        Authentication authentication = createAuthentication(user);
        return jwtUtil.generateTempToken(authentication);
    }

    // 인증 객체 생성
    public Authentication createAuthentication(User user) {
        CustomUserDetails userDetails = new CustomUserDetails(user);
        return new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
    }

    // 로그아웃 처리
    public void logout() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                throw BaseException.type(AuthErrorCode.USER_NOT_AUTHENTICATED);
            }

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            Long userId = userDetails.getId();
            
            String token = jwtUtil.getCurrentToken();
            
            if (!jwtUtil.isAccessToken(token)) {
                throw BaseException.type(AuthErrorCode.INVALID_TOKEN);
            }
            
            long remainingTime = jwtUtil.getRemainingTimeInSeconds(token);
            
            // Redis에 액세스 토큰 블랙리스트 추가 (남은 시간만큼)
            if (remainingTime > 0) {
                addToBlacklist(token, remainingTime);
            }
            
            // 리프레시 토큰 삭제
            logoutUser(userId.toString());
            
            // SecurityContext 정리
            SecurityContextHolder.clearContext();
            
            log.info("사용자 로그아웃 완료: userId={}", userId);

        } catch (Exception e) {
            log.error("로그아웃 처리 중 오류 발생: {}", e.getMessage());
            throw BaseException.type(AuthErrorCode.LOGOUT_FAILED);
        }
    }

    // 블랙리스트 체크
    public boolean isTokenBlacklisted(String token) {
        return isBlacklisted(token);
    }

    // 토큰 재발급 (Refresh Token Rotation)
    public ReissueTokenResponse reissueToken(String refreshToken) {
        try {
            if (!jwtUtil.validateToken(refreshToken)) {
                throw BaseException.type(AuthErrorCode.INVALID_REFRESH_TOKEN);
            }
            
            if (!jwtUtil.isRefreshToken(refreshToken)) {
                throw BaseException.type(AuthErrorCode.INVALID_REFRESH_TOKEN);
            }
            
            if (isBlacklisted(refreshToken)) {
                throw BaseException.type(AuthErrorCode.INVALID_REFRESH_TOKEN);
            }
            
            Long userId = jwtUtil.getUserIdFromToken(refreshToken);
            String storedRefreshToken = getRefreshToken(userId.toString());
            
            if (storedRefreshToken == null) {
                throw BaseException.type(AuthErrorCode.REFRESH_TOKEN_NOT_FOUND);
            }
            
            if (!refreshToken.equals(storedRefreshToken)) {
                throw BaseException.type(AuthErrorCode.REFRESH_TOKEN_MISMATCH);
            }
            
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> BaseException.type(AuthErrorCode.USER_NOT_AUTHENTICATED));
            
            // 기존 리프레시 토큰 삭제 (Refresh Token Rotation)
            deleteRefreshToken(userId.toString());
            
            // 새 토큰 발급 및 저장
            TokenDto token = createAndStoreTokens(user);
            
            log.info("토큰 재발급 완료: userId={}", userId);
            
            return ReissueTokenResponse.of(token.getAccessToken(), token.getRefreshToken(), token.getAccessTokenExp());
        } catch (BaseException e) {
            throw e;
        } catch (Exception e) {
            log.error("토큰 재발급 중 오류 발생: {}", e.getMessage());
            throw BaseException.type(AuthErrorCode.INVALID_REFRESH_TOKEN);
        }
    }
    
    // 액세스 토큰을 블랙리스트에 추가
    public void addToBlacklist(String token, long expirationTimeInSeconds) {
        String key = BLACKLIST_PREFIX + token;
        redisTemplate.opsForValue().set(key, "blacklisted", Duration.ofSeconds(expirationTimeInSeconds));
        log.info("토큰이 블랙리스트에 추가됨: {}", token);
    }

    // 리프레시 토큰을 저장
    public void saveRefreshToken(String userId, String refreshToken, long expirationTimeInSeconds) {
        String key = REFRESH_TOKEN_PREFIX + userId;
        redisTemplate.opsForValue().set(key, refreshToken, Duration.ofSeconds(expirationTimeInSeconds));
        log.info("리프레시 토큰 저장됨: userId={}", userId);
    }

    // 토큰이 블랙리스트에 있는지 확인
    public boolean isBlacklisted(String token) {
        String key = BLACKLIST_PREFIX + token;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    // 리프레시 토큰 조회
    public String getRefreshToken(String userId) {
        String key = REFRESH_TOKEN_PREFIX + userId;
        return redisTemplate.opsForValue().get(key);
    }

    // 리프레시 토큰 삭제
    public void deleteRefreshToken(String userId) {
        String key = REFRESH_TOKEN_PREFIX + userId;
        redisTemplate.delete(key);
        log.info("리프레시 토큰 삭제됨: userId={}", userId);
    }

    // 사용자의 모든 토큰 삭제 (로그아웃)
    public void logoutUser(String userId) {
        deleteRefreshToken(userId);
    }
}
