package dgu.umc_app.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record ResetPasswordRequest(
        @NotBlank(message = "새 비밀번호는 필수입니다.")
        @Schema(example = "password123!!")
        String newPassword
) {
}
