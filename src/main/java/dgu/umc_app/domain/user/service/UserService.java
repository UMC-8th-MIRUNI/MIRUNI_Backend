package dgu.umc_app.domain.user.service;

import org.springframework.stereotype.Service;
import dgu.umc_app.domain.user.repository.UserRepository;
import dgu.umc_app.global.common.JwtUtil;
import dgu.umc_app.global.exception.CommonErrorCode;
import dgu.umc_app.global.exception.ConflictException;
import dgu.umc_app.global.exception.EntityNotFoundException;
import dgu.umc_app.global.exception.InvalidValueException;
import dgu.umc_app.domain.user.entity.User;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import dgu.umc_app.domain.user.dto.request.UserSignupRequest;
import dgu.umc_app.domain.user.dto.request.UserLoginRequest;
import dgu.umc_app.domain.user.dto.response.UserSignupResponse;
import dgu.umc_app.domain.user.dto.response.UserLoginResponse;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public UserSignupResponse signup(UserSignupRequest userSignupRequest) {
        if (userRepository.existsByEmail(userSignupRequest.email())) {
            throw new ConflictException(CommonErrorCode.USER_EMAIL_EXIST);
        }

        String encodedPassword = passwordEncoder.encode(userSignupRequest.password());

        User user = userSignupRequest.toEntity(encodedPassword);

        User savedUser = userRepository.save(user);

        return UserSignupResponse.from(savedUser);
    }

    public UserLoginResponse login(UserLoginRequest userLoginRequest) {
        User user = userRepository.findByEmail(userLoginRequest.email())
                .orElseThrow(() -> new EntityNotFoundException(CommonErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(userLoginRequest.password(), user.getPassword())) {
            throw new InvalidValueException(CommonErrorCode.USER_WRONG_PASSWORD);
        }

        String accessToken = jwtUtil.generateAccessToken(user.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

        return UserLoginResponse.from(user, accessToken, refreshToken);
    }
}
