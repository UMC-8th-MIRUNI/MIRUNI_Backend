package dgu.umc_app.domain.user.dto.response;

public record PasswordResetResponse(
    String message
) {
    public static PasswordResetResponse of(String message) {
        return new PasswordResetResponse(message);
    }
}
