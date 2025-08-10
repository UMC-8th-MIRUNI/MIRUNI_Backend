package dgu.umc_app.domain.user.service;

import dgu.umc_app.domain.user.dto.response.UserInfoResponse;
import dgu.umc_app.domain.user.entity.User;
import dgu.umc_app.domain.user.exception.UserErrorCode;
import dgu.umc_app.domain.user.repository.UserRepository;
import dgu.umc_app.global.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class UserQueryService {

    private final UserRepository userRepository;

    public UserInfoResponse getUserInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> BaseException.type(UserErrorCode.USER_NOT_FOUND));

        return UserInfoResponse.from(user);
    }

    public void duplicateCheck(String email) {
        if (userRepository.existsByEmail(email)) {
            throw BaseException.type(UserErrorCode.USER_EMAIL_EXIST);
        }
    }

}
