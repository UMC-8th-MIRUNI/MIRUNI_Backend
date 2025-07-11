package dgu.umc_app.domain.user.dto.response;

import dgu.umc_app.domain.user.entity.User;
import lombok.Builder;

@Builder
public record UserLoginResponse(
        Long id,
        String name,
        String email,
        String nickname,
        String accessToken,
        String refreshToken
) {
    public static UserLoginResponse from(User user, String accessToken, String refreshToken) {
        return UserLoginResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
} 