package dgu.umc_app.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ChangePasswordRequest(
    @NotBlank(message = "현재 비밀번호는 필수입니다.")
    String currentPassword,
    
    @NotBlank(message = "새 비밀번호는 필수입니다.")
    String newPassword
) {
}
