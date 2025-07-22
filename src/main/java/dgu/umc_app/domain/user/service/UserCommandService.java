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
import dgu.umc_app.domain.user.dto.GoogleUserInfoDto;
import dgu.umc_app.domain.user.dto.request.GoogleLoginRequest;
import dgu.umc_app.domain.user.dto.response.GoogleLoginResponse;
import dgu.umc_app.domain.user.entity.OauthProvider;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@Transactional
@RequiredArgsConstructor
public class UserCommandService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserResponse signup(UserSignupRequest userSignupRequest) {
        if (userRepository.existsByEmail(userSignupRequest.email())) {
            throw BaseException.type(UserErrorCode.USER_EMAIL_EXIST);
        }

        String encodedPassword = passwordEncoder.encode(userSignupRequest.password());
        User user = userSignupRequest.toEntity(encodedPassword);
        userRepository.save(user);

        return issueTokenResponse(user);
    }

    public UserResponse login(UserLoginRequest userLoginRequest) {
        User user = userRepository.findByEmail(userLoginRequest.email())
                .orElseThrow(() -> BaseException.type(UserErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(userLoginRequest.password(), user.getPassword())) {
            throw BaseException.type(UserErrorCode.USER_WRONG_PASSWORD);
        }

        return issueTokenResponse(user);
    }

    public GoogleLoginResponse loginWithGoogle(GoogleLoginRequest request) {
        
        GoogleUserInfoDto googleUserInfo = verifyGoogleIdToken(request.googleIdToken());

        User user = userRepository.findByEmail(googleUserInfo.email()).orElse(null);
        boolean isNewUser = false;

        if (user == null) {
            user = User.builder()
                .email(googleUserInfo.email())
                .name(googleUserInfo.name())
                .oauthProvider(OauthProvider.GOOGLE)
                .password("") // 소셜로그인은 비밀번호 없음
                .nickname(googleUserInfo.name())
                .phoneNumber("")
                .passwordExpired(false)
                .agreedPrivacyPolicy(false)
                .userPreference("")
                .peanutCount(0)
                .build();
            userRepository.save(user);
            isNewUser = true;
        }

        String accessToken = jwtUtil.generateAccessToken(user.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());
        long expiresIn = jwtUtil.getAccessTokenExpirationInSeconds();

        return GoogleLoginResponse.of(
            accessToken,
            refreshToken,
            expiresIn,
            isNewUser
        );
    }

    private UserResponse issueTokenResponse(User user) {
        String accessToken = jwtUtil.generateAccessToken(user.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());
        long accessTokenExp = jwtUtil.getAccessTokenExpirationInSeconds();
        long refreshTokenExp = jwtUtil.getRefreshTokenExpirationInSeconds();

        return UserResponse.of(accessToken, refreshToken, accessTokenExp, refreshTokenExp);
    }

    private GoogleUserInfoDto verifyGoogleIdToken(String idToken) {
        try {
            String url = "https://oauth2.googleapis.com/tokeninfo?id_token=" + idToken;
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            if (response.getStatusCode() != HttpStatus.OK) {
                throw BaseException.type(UserErrorCode.INVALID_SOCIAL_TOKEN);
            }
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode node = objectMapper.readTree(response.getBody());
            String email = node.get("email").asText();
            String name = node.has("name") ? node.get("name").asText() : "";
            return GoogleUserInfoDto.of(email, name);
        } catch (Exception e) {
            throw BaseException.type(UserErrorCode.INVALID_SOCIAL_TOKEN);
        }
    }
}
