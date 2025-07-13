package dgu.umc_app.domain.user.dto.response;

import lombok.Builder;

@Builder
public record UserLoginResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        Long accessTokenExpiresIn,
        Long refreshTokenExpiresIn
) {
    public static UserLoginResponse from(String accessToken, String refreshToken, long accessTokenExp, long refreshTokenExp) {
        return UserLoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .accessTokenExpiresIn(accessTokenExp)
                .refreshTokenExpiresIn(refreshTokenExp)
                .build();
    }
} 