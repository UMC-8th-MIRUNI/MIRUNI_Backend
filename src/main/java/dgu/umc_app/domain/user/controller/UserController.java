package dgu.umc_app.domain.user.controller;

import dgu.umc_app.domain.user.dto.UserResponseDto;
import dgu.umc_app.domain.user.service.UserQueryService;
import dgu.umc_app.global.authorize.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController implements UserApi {

    private final UserQueryService userQueryService;

    @Override
    @GetMapping("/mypage")
    public UserResponseDto getUserInfo(@LoginUser Long userId) {
        return userQueryService.getUserInfo(userId);
    }
}
