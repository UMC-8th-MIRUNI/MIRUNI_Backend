package dgu.umc_app.domain.user.controller;

import dgu.umc_app.domain.user.dto.response.UserInfoResponse;
import dgu.umc_app.domain.user.dto.response.UserSurveyResponse;
import dgu.umc_app.domain.user.entity.ProfileImage;
import dgu.umc_app.domain.user.service.UserCommandService;
import dgu.umc_app.domain.user.service.UserQueryService;
import dgu.umc_app.global.authorize.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController implements UserApi {

    private final UserQueryService userQueryService;
    private final UserCommandService userCommandService;

    @Override
    @GetMapping("/mypage")
    public UserInfoResponse getUserInfo(@LoginUser Long userId) {
        return userQueryService.getUserInfo(userId);
    }

    @Override
    @PutMapping("/profileImage")
    public UserInfoResponse updateProfileImage(@LoginUser Long userId, ProfileImage profileImage) {
        return userCommandService.updateProfileImage(userId, profileImage);
    }

    @GetMapping("/survey")
    public UserSurveyResponse getUserSurveyResult(@LoginUser Long userId) {
        return userQueryService.getUserSurveyResult(userId);
    }
}
