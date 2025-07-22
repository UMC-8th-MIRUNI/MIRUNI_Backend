package dgu.umc_app.domain.user.dto.response;

import lombok.Builder;

@Builder
public record GoogleLoginResponse(
    String accessToken,
    String refreshToken,
    String tokenType,
    long expiresIn,
    boolean isNewUser
) {
    public static GoogleLoginResponse of(String accessToken, String refreshToken, long expiresIn, boolean isNewUser) {
        return GoogleLoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(expiresIn)
                .isNewUser(isNewUser)
                .build();
    }
} 