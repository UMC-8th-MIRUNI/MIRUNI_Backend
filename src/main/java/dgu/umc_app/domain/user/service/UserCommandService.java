package dgu.umc_app.domain.user.service;

import dgu.umc_app.domain.user.dto.response.UserInfoResponse;
import dgu.umc_app.domain.user.dto.response.VerifyResponse;
import dgu.umc_app.domain.user.entity.ProfileImage;
import org.springframework.stereotype.Service;
import dgu.umc_app.domain.user.repository.UserRepository;
import dgu.umc_app.global.authorize.TokenService;
import dgu.umc_app.global.exception.BaseException;
import dgu.umc_app.global.exception.CommonErrorCode;
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
import dgu.umc_app.domain.user.dto.response.SurveyResponse;
import dgu.umc_app.domain.user.entity.OauthProvider;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dgu.umc_app.domain.user.dto.request.KakaoLoginRequest;
import dgu.umc_app.domain.user.dto.request.GoogleSignUpRequest;
import dgu.umc_app.domain.user.validator.UserValidator;
import lombok.extern.slf4j.Slf4j;
import dgu.umc_app.domain.user.dto.request.KakaoSignUpRequest;
import dgu.umc_app.domain.user.dto.request.PasswordResetRequest;
import dgu.umc_app.domain.user.dto.request.VerifyResetCodeRequest;
import dgu.umc_app.domain.user.dto.request.ResetPasswordRequest;
import org.springframework.data.redis.core.RedisTemplate;
import java.time.Duration;
import java.util.Random;
import dgu.umc_app.domain.user.dto.request.SurveyRequest;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserCommandService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final UserValidator userValidator;
    // == 비밀번호 재설정 관련 필드 추가 == //
    private final RedisTemplate<String, String> redisTemplate;
    private final EmailService emailService;

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
        return tokenService.issueTokenResponse(user);
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
        // user.activate(); -> 설문 후 active로 

        return issueTokenResponse(user);
    }

    private User findOrCreateUser(AuthUserInfoDto userInfo, OauthProvider provider) {
        return userRepository.findByEmail(userInfo.email())
                .orElseGet(() -> userRepository.save(userInfo.toSocialUser(provider)));
    }

    private String generateTempToken(User user) {
        return tokenService.generateTempTokenForUser(user);
    }

    private AuthLoginResponse generateLoginTokens(User user, boolean isNewUser) {
        return tokenService.generateLoginTokens(user, isNewUser);
    }

    public void logout() {
        tokenService.logout();
    }

    public void withdrawUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> BaseException.type(UserErrorCode.USER_NOT_FOUND));

        if (user.isDeleted()) {
            throw BaseException.type(UserErrorCode.USER_ALREADY_DELETED);
        }

        user.delete();
        
        tokenService.logout();

        log.info("회원 탈퇴 완료: userId={}", userId);
    }

    // 비밀번호 변경 (기존 비밀번호 알고 있을때)
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> BaseException.type(UserErrorCode.USER_NOT_FOUND));

        if (user.isSocialUser()) {
            throw BaseException.type(UserErrorCode.SOCIAL_USER_PASSWORD_CHANGE);
        }

        if (!user.hasPassword()) {
            throw BaseException.type(UserErrorCode.USER_WRONG_PASSWORD);
        }

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw BaseException.type(UserErrorCode.USER_WRONG_PASSWORD);
        }

        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw BaseException.type(UserErrorCode.SAME_PASSWORD);
        }

        // 비밀번호 암호화
        user.updatePassword(passwordEncoder.encode(newPassword));

        tokenService.logout();

        log.info("비밀번호 변경 완료: userId={}", userId);
    }
    
    // 비밀번호 재설정 요청 -> 이메일로 인증 코드 전송
    public void requestPasswordReset(PasswordResetRequest request) {
        if (!userRepository.existsByEmail(request.email())) {
            throw BaseException.type(UserErrorCode.USER_EMAIL_NOT_FOUND);
        }

        String verificationCode = generateVerificationCode();
        
        String redisKey = "email_verification:" + request.email();
        redisTemplate.opsForValue().set(redisKey, verificationCode, Duration.ofMinutes(10));
        
        log.info("인증 코드 생성 완료 - 이메일: {}, 코드: {}", request.email(), verificationCode);

        emailService.sendPasswordResetCode(request.email(), verificationCode);
    }

    // 이메일로 전송된 인증 코드 검증
    public VerifyResponse verifyResetCode(VerifyResetCodeRequest request) {
        String redisKey = "email_verification:" + request.email();
        String storedCode = redisTemplate.opsForValue().get(redisKey);
        
        if (storedCode == null) {
            throw BaseException.type(UserErrorCode.VERIFICATION_CODE_EXPIRED);
        }
        
        if (!storedCode.equals(request.code())) {
            throw BaseException.type(UserErrorCode.INVALID_VERIFICATION_CODE);
        }
        
        // 검증 완료 토큰 생성
        String resetToken = generateResetToken(request.email());
        String tokenKey = "reset_token:" + resetToken;
        redisTemplate.opsForValue().set(tokenKey, request.email(), Duration.ofMinutes(10));
        
        log.info("인증 코드 검증 성공 - 이메일: {}, 토큰: {}", request.email(), resetToken);
        return new VerifyResponse(resetToken);
    }

    // 비밀번호 재설정(비밀번호 잊었을 때)
    public void resetPassword(String resetToken, ResetPasswordRequest request) {
        
        //토큰으로 이메일 확인
        String tokenKey = "reset_token:" + resetToken;
        String email = redisTemplate.opsForValue().get(tokenKey);

        if (email == null) {
            throw BaseException.type(UserErrorCode.RESET_TOKEN_EXPIRED);
        }
        
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> BaseException.type(UserErrorCode.USER_NOT_FOUND));

        if (user.isSocialUser()) {
            throw BaseException.type(UserErrorCode.SOCIAL_USER_PASSWORD_CHANGE);
        }

        String encodedNewPassword = passwordEncoder.encode(request.newPassword());
        user.updatePassword(encodedNewPassword);

        redisTemplate.delete(tokenKey);
        redisTemplate.delete("email_verification:" + email);

        tokenService.logoutUser(user.getId().toString());
        
        log.info("비밀번호 재설정 완료 - 이메일: {}", email);
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = 1000 + random.nextInt(9000);
        return String.valueOf(code);
    }
    
    private String generateResetToken(String email) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String combined = email + ":" + timestamp;
        
        return java.util.Base64.getEncoder().encodeToString(combined.getBytes()).substring(0, 32);
    }
    
    public UserInfoResponse updateProfileImage(Long userId, ProfileImage profileImage) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> BaseException.type(UserErrorCode.USER_NOT_FOUND));

        user.updateProfileImage(profileImage);

        return UserInfoResponse.from(user);
    }

    public SurveyResponse survey(SurveyRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> BaseException.type(UserErrorCode.USER_NOT_FOUND));

        if (user.isSurveyCompleted()) {
            throw BaseException.type(UserErrorCode.SURVEY_ALREADY_COMPLETED);
        }

        // User 엔티티에 직접 survey 정보 저장 (비트마스크 방식)
        user.updateSurveyInfo(request.situations(), request.level(), request.reasons());
        
        log.info("설문조사 완료: userId={}, Q1: {}, Q2: {}, Q3: {}", 
                userId, request.situations(), request.level(), request.reasons());

        return SurveyResponse.of(
            "설문조사가 완료되었습니다!",
            LocalDateTime.now(),
            "COMPLETED"
        );    
    }


}
