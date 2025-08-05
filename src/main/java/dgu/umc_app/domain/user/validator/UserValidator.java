package dgu.umc_app.domain.user.validator;

import dgu.umc_app.domain.user.entity.Status;
import dgu.umc_app.domain.user.exception.AuthErrorCode;
import dgu.umc_app.domain.user.exception.UserErrorCode;
import dgu.umc_app.domain.user.repository.UserRepository;
import dgu.umc_app.global.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserValidator {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public void matchPassword(String password, String userPassword) {
        if (!passwordEncoder.matches(password, userPassword)) {
            throw BaseException.type(UserErrorCode.USER_WRONG_PASSWORD);
        }
    }

    public void existEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw BaseException.type(UserErrorCode.USER_EMAIL_EXIST);
        }
    }

    public void checkPendingUser(Status status) {
        if (!status.equals(Status.PENDING)) {
            throw BaseException.type(AuthErrorCode.ALREADY_COMPLETED_SIGNUP);
        }
    }
} 