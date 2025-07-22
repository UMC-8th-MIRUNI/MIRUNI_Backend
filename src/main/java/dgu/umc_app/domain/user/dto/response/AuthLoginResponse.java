package dgu.umc_app.domain.user.dto.response;

import lombok.Builder;

@Builder
public record AuthLoginResponse(
    String accessToken,
    String refreshToken,
    String tokenType,
    long expiresIn,
    boolean isNewUser
) {
    public static AuthLoginResponse of(String accessToken, String refreshToken, long expiresIn, boolean isNewUser) {
        return AuthLoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(expiresIn)
                .isNewUser(isNewUser)
                .build();
    }
} 