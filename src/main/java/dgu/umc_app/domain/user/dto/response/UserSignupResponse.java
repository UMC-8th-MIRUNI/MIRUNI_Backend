package dgu.umc_app.domain.user.dto.response;

import dgu.umc_app.domain.user.entity.User;
import lombok.Builder;

@Builder
public record UserSignupResponse(
        Long id,
        String name,
        String email,
        String nickname
) {
    public static UserSignupResponse from(User user) {
        return UserSignupResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .build();
    }
}