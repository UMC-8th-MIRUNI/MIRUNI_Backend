package dgu.umc_app.domain.user.dto.response;

import dgu.umc_app.domain.user.entity.ProfileImage;
import dgu.umc_app.domain.user.entity.User;

public record UserInfoResponse(
        Long id,
        String name,
        String birthday,
        String email,
        String phoneNumber,
        String nickname,
        String oauthProvider,
        ProfileImage profileImage
) {
    public static UserInfoResponse from(User user) {
        return new UserInfoResponse(
                user.getId(),
                user.getName(),
                user.getBirthday(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getNickname(),
                user.getOauthProvider() != null ? user.getOauthProvider().name() : null,
                user.getProfileImage()
        );
    }
}