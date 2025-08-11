package dgu.umc_app.domain.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record VerifyResetCodeRequest(
    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    String email,
    
    @NotBlank(message = "인증 코드는 필수입니다.")
    @Pattern(regexp = "^[0-9]{4}$", message = "인증 코드는 4자리 숫자여야 합니다.")
    String code
) {
}
