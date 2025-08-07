package dgu.umc_app.domain.user.dto.response;

public record ReissueTokenResponse(
    String accessToken,
    String refreshToken,
    String tokenType,
    long expiresIn
) {
    public static ReissueTokenResponse of(String accessToken, String refreshToken, long expiresIn) {
        return new ReissueTokenResponse(accessToken, refreshToken, "Bearer", expiresIn);
    }
}
