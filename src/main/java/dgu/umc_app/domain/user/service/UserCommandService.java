package dgu.umc_app.domain.user.service;

import org.springframework.stereotype.Service;
import dgu.umc_app.domain.user.repository.UserRepository;
import dgu.umc_app.global.common.JwtUtil;
import dgu.umc_app.global.exception.BaseException;
import dgu.umc_app.domain.user.entity.User;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import dgu.umc_app.domain.user.dto.request.UserSignupRequest;
import dgu.umc_app.domain.user.dto.request.UserLoginRequest;
import dgu.umc_app.domain.user.dto.response.UserResponse;
import dgu.umc_app.domain.user.exception.UserErrorCode;
import lombok.RequiredArgsConstructor;
import dgu.umc_app.domain.user.dto.AuthUserInfoDto;
import dgu.umc_app.domain.user.dto.request.GoogleLoginRequest;
import dgu.umc_app.domain.user.dto.response.AuthLoginResponse;
import dgu.umc_app.domain.user.entity.OauthProvider;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dgu.umc_app.domain.user.exception.AuthErrorCode;
import dgu.umc_app.domain.user.dto.request.KakaoLoginRequest;
import dgu.umc_app.domain.user.dto.request.GoogleSignUpRequest;
import dgu.umc_app.domain.user.validator.UserValidator;
import lombok.extern.slf4j.Slf4j;
import dgu.umc_app.domain.user.dto.request.KakaoSignUpRequest;
import dgu.umc_app.global.authorize.CustomUserDetails;
import dgu.umc_app.global.service.TokenBlacklistService;
import dgu.umc_app.global.exception.CommonErrorCode;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserCommandService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserValidator userValidator;
    private final TokenBlacklistService tokenBlacklistService;

    public UserResponse signup(UserSignupRequest userSignupRequest) {

        userValidator.existEmail(userSignupRequest.email());

        String encodedPassword = passwordEncoder.encode(userSignupRequest.password());
        User user = userSignupRequest.toEntity(encodedPassword);
        userRepository.save(user);

        return issueTokenResponse(user);
    }

    public UserResponse login(UserLoginRequest userLoginRequest) {
        User user = userRepository.findByEmail(userLoginRequest.email())
                .orElseThrow(() -> BaseException.type(UserErrorCode.USER_NOT_FOUND));

        userValidator.matchPassword(userLoginRequest.password(), user.getPassword());

        return issueTokenResponse(user);
    }

    public AuthLoginResponse loginWithGoogle(GoogleLoginRequest request) {
        
        AuthUserInfoDto googleUserInfo = verifyGoogleIdToken(request.googleIdToken());
        return processSocialLogin(googleUserInfo, OauthProvider.GOOGLE);
    }

    public AuthLoginResponse loginWithKakao(KakaoLoginRequest request) {
        AuthUserInfoDto kakaoUserInfo = verifyKakaoAccessToken(request.kakaoAccessToken());
        return processSocialLogin(kakaoUserInfo, OauthProvider.KAKAO);
    }

    private UserResponse issueTokenResponse(User user) {

        Authentication authentication = createAuthentication(user);
        
        String accessToken = jwtUtil.generateAccessToken(authentication);
        String refreshToken = jwtUtil.generateRefreshToken(authentication);
        long accessTokenExp = jwtUtil.getAccessTokenExpirationInSeconds();
        long refreshTokenExp = jwtUtil.getRefreshTokenExpirationInSeconds();

        // 리프레시 토큰을 Redis에 저장
        tokenBlacklistService.saveRefreshToken(user.getId().toString(), refreshToken, refreshTokenExp);

        return UserResponse.of(accessToken, refreshToken, accessTokenExp, refreshTokenExp);
    }

    private AuthUserInfoDto verifyGoogleIdToken(String idToken) {
        try {
            String url = "https://oauth2.googleapis.com/tokeninfo?id_token=" + idToken;
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            
            if (response.getStatusCode() != HttpStatus.OK) {
                throw BaseException.type(UserErrorCode.INVALID_SOCIAL_TOKEN);
            }
            
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode node = objectMapper.readTree(response.getBody());

            // aud 값 검증로직 -> 추후에 추가
            // String aud = node.get("aud").asText();
            // String expectedClientId = "YOUR_GOOGLE_CLIENT_ID"; // -> Google Console에서 발급받은 client_id로 바꿔야 함
            // if (!expectedClientId.equals(aud)) {
            //     throw BaseException.type(UserErrorCode.INVALID_SOCIAL_TOKEN);
            // }

            String email = node.get("email").asText();
            String name = node.has("name") ? node.get("name").asText() : "";
            
            return AuthUserInfoDto.of(email, name);

        } catch (Exception e) {
            throw BaseException.type(UserErrorCode.INVALID_SOCIAL_TOKEN);
        }
    }

    private AuthUserInfoDto verifyKakaoAccessToken(String kakaoAccessToken) {
        try {
            String url = "https://kapi.kakao.com/v2/user/me";
            RestTemplate restTemplate = new RestTemplate();
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.set("Authorization", "Bearer " + kakaoAccessToken);
            org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            
            if (response.getStatusCode() != HttpStatus.OK) {
                throw BaseException.type(UserErrorCode.INVALID_SOCIAL_TOKEN);
            }

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode node = objectMapper.readTree(response.getBody());

            String email = node.at("/kakao_account/email").asText("");
            String name = node.at("/properties/nickname").asText("");
            
            if (email.isEmpty()) {
                throw BaseException.type(UserErrorCode.INVALID_SOCIAL_TOKEN);
            }

            return AuthUserInfoDto.of(email, name);

        } catch (Exception e) {
            throw BaseException.type(UserErrorCode.INVALID_SOCIAL_TOKEN);
        }
    }

    public UserResponse googleSignUp(GoogleSignUpRequest request, Long userId) {
        return processSocialSignUp(userId, user -> {
            user.updateGoogleSignUpInfo(
                request.name(),
                request.birthday(),
                request.phoneNumber(),
                request.agreedPrivacyPolicy(),
                request.nickname()
            );
        });
    }

    public UserResponse kakaoSignUp(KakaoSignUpRequest request, Long userId) {
        return processSocialSignUp(userId, user -> {
            user.updateKakaoSignUpInfo(
                request.name(),
                request.birthday(),
                request.phoneNumber(),
                request.agreedPrivacyPolicy(),
                request.nickname()
            );
        });
    }

    // 소셜 로그인 공통 처리 로직
    private AuthLoginResponse processSocialLogin(AuthUserInfoDto userInfo, OauthProvider provider) {
        User user = findOrCreateUser(userInfo, provider);
        boolean isNewUser = userRepository.findByEmail(userInfo.email()).isEmpty();

        if (user.isDeleted()) {
            user.restore();
        }

        if (user.isPending()) {
            String tempToken = generateTempToken(user);
            return AuthLoginResponse.signUpNeeded(tempToken, isNewUser);
        }

        return generateLoginTokens(user, isNewUser);
    }

    // 소셜 회원가입 공통 처리 로직
    private UserResponse processSocialSignUp(Long userId, java.util.function.Consumer<User> updateUserInfo) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> BaseException.type(UserErrorCode.USER_NOT_FOUND));
        
        userValidator.checkPendingUser(user.getStatus());

        updateUserInfo.accept(user);
        user.activate();

        User savedUser = userRepository.save(user);
        return issueTokenResponse(savedUser);
    }

    private User findOrCreateUser(AuthUserInfoDto userInfo, OauthProvider provider) {
        return userRepository.findByEmail(userInfo.email())
                .orElseGet(() -> userRepository.save(userInfo.toSocialUser(provider)));
    }

    private String generateTempToken(User user) {
        Authentication authentication = createAuthentication(user);
        return jwtUtil.generateTempToken(authentication);
    }

    private AuthLoginResponse generateLoginTokens(User user, boolean isNewUser) {
        
        Authentication authentication = createAuthentication(user);
        
        String accessToken = jwtUtil.generateAccessToken(authentication);
        String refreshToken = jwtUtil.generateRefreshToken(authentication);
        long accessTokenExp = jwtUtil.getAccessTokenExpirationInSeconds();
        long refreshTokenExp = jwtUtil.getRefreshTokenExpirationInSeconds();
        
        // 리프레시 토큰을 Redis에 저장
        tokenBlacklistService.saveRefreshToken(user.getId().toString(), refreshToken, refreshTokenExp);
        
        return AuthLoginResponse.login(accessToken, refreshToken, accessTokenExp, refreshTokenExp, isNewUser);
    }

    private Authentication createAuthentication(User user) {
        CustomUserDetails userDetails = new CustomUserDetails(user);
        return new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
    }

    public void logout() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                throw BaseException.type(AuthErrorCode.USER_NOT_AUTHENTICATED);
            }

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            Long userId = userDetails.getId();
            
            String token = getCurrentToken();
            long expirationTime = jwtUtil.getAccessTokenExpirationInSeconds();
            
            // Redis에 엑세스 토큰 블랙리스트 추가
            tokenBlacklistService.addToBlacklist(token, expirationTime);
            
            // 리프레시 토큰 삭제
            tokenBlacklistService.logoutUser(userId.toString());
            
            log.info("사용자 로그아웃 완료: userId={}", userId);

        } catch (Exception e) {
            log.error("로그아웃 처리 중 오류 발생: {}", e.getMessage());
            throw BaseException.type(AuthErrorCode.LOGOUT_FAILED);
        }
    }

    private String getCurrentToken() {
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
}
