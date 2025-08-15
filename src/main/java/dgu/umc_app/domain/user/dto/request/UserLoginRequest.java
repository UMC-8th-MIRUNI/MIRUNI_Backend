package dgu.umc_app.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;

public record UserLoginRequest(
        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        @Schema(example = "dhzktldh@gmail.com")
        String email,

        @NotBlank(message = "비밀번호는 필수입니다.")
        @Schema(example = "password123!")
        String password
) {
} 