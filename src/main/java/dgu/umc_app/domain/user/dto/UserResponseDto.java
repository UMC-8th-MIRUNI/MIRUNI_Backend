package dgu.umc_app.domain.user.dto;

import dgu.umc_app.domain.user.entity.User;
import lombok.Builder;

@Builder
public record UserResponseDto(
        Long id,
        String name,
        String birthday,
        String email,
        String phoneNumber,
        String nickname,
        boolean agreedPrivacyPolicy,
        String oauthProvider
) {
    public static UserResponseDto from(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .birthday(user.getBirthday())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .nickname(user.getNickname())
                .agreedPrivacyPolicy(user.isAgreedPrivacyPolicy())
                .oauthProvider(user.getOauthProvider() != null ? user.getOauthProvider().name() : null)
                .build();
    }
}
