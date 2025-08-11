package dgu.umc_app.domain.user.dto.response;

public record ReissueTokenResponse(
    String accessToken,
    String refreshToken,
    String tokenType,
    long accessTokenExp,
    long refreshTokenExp
) {
    public static ReissueTokenResponse of(String accessToken, String refreshToken, long accessTokenExp, long refreshTokenExp) {
        return new ReissueTokenResponse(accessToken, refreshToken, "Bearer", accessTokenExp, refreshTokenExp);
    }
}
