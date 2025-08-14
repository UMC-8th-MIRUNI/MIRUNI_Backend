package dgu.umc_app.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ReissueTokenRequest(
    @NotBlank(message = "리프레시 토큰은 필수입니다.")
    String refreshToken
) {
}
