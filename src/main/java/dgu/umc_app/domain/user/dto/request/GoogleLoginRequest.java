package dgu.umc_app.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;

public record GoogleLoginRequest(
    @NotBlank(message = "구글 ID 토큰은 필수입니다.")
    String googleIdToken
) {
} 