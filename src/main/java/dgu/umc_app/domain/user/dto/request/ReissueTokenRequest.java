package dgu.umc_app.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record ReissueTokenRequest(
        @NotBlank(message = "리프레시 토큰은 필수입니다.")
        @Schema(example = "eyJhbGciOiJIUzI1NiJ9.tenup...")
        String refreshToken
) {
}
