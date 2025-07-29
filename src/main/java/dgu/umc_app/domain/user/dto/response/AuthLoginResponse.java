package dgu.umc_app.domain.user.dto.response;

import lombok.Builder;

@Builder
public record AuthLoginResponse(
    String accessToken,
    String refreshToken,
    String tokenType,
    Long accessTokenExpiresIn,
    Long refreshTokenExpiresIn,
    boolean isNewUser,
    boolean isPending
) {
    public static AuthLoginResponse login(String accessToken, String refreshToken, long accessTokenExp, long refreshTokenExp, boolean isNewUser) {
        return AuthLoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .accessTokenExpiresIn(accessTokenExp)
                .refreshTokenExpiresIn(refreshTokenExp)
                .isNewUser(isNewUser)
                .isPending(false)
                .build();
    }

    public static AuthLoginResponse signUpNeeded(String tempToken, boolean isNewUser) {
        return AuthLoginResponse.builder()
                .accessToken(tempToken)
                .refreshToken(null)
                .tokenType("Bearer")
                .accessTokenExpiresIn(300L) // 5분 임시 토큰
                .refreshTokenExpiresIn(null)
                .isNewUser(isNewUser)
                .isPending(true)
                .build();
    }
} 