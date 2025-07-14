package dgu.umc_app.domain.user.service;

import org.springframework.stereotype.Service;
import dgu.umc_app.domain.user.repository.UserRepository;
import dgu.umc_app.global.common.JwtUtil;
import dgu.umc_app.global.exception.BaseException;
import dgu.umc_app.global.exception.CommonErrorCode;
import dgu.umc_app.domain.user.entity.User;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import dgu.umc_app.domain.user.dto.request.UserSignupRequest;
import dgu.umc_app.domain.user.dto.request.UserLoginRequest;
import dgu.umc_app.domain.user.dto.response.UserResponse;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public UserResponse signup(UserSignupRequest userSignupRequest) {
        if (userRepository.existsByEmail(userSignupRequest.email())) {
            throw BaseException.type(CommonErrorCode.USER_EMAIL_EXIST);
        }

        String encodedPassword = passwordEncoder.encode(userSignupRequest.password());
        User user = userSignupRequest.toEntity(encodedPassword);
        userRepository.save(user);

        return issueTokenResponse(user);
    }

    public UserResponse login(UserLoginRequest userLoginRequest) {
        User user = userRepository.findByEmail(userLoginRequest.email())
                .orElseThrow(() -> BaseException.type(CommonErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(userLoginRequest.password(), user.getPassword())) {
            throw BaseException.type(CommonErrorCode.USER_WRONG_PASSWORD);
        }

        return issueTokenResponse(user);
    }

    private UserResponse issueTokenResponse(User user) {
        String accessToken = jwtUtil.generateAccessToken(user.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());
        long accessTokenExp = jwtUtil.getAccessTokenExpirationInSeconds();
        long refreshTokenExp = jwtUtil.getRefreshTokenExpirationInSeconds();

        return UserResponse.of(accessToken, refreshToken, accessTokenExp, refreshTokenExp);
    }
}
