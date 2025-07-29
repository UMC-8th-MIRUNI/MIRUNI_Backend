package dgu.umc_app.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

public record GoogleSignUpRequest(
    @NotBlank(message = "이름은 필수입니다.")
    @Size(max = 20, message = "이름은 20자 이하여야 합니다.")
    String name,

    @NotBlank(message = "생년월일은 필수입니다.")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "올바른 생년월일 형식이 아닙니다.")
    String birthday,
    
    @NotBlank(message = "전화번호는 필수입니다.")
    @Pattern(regexp = "^[0-9-+()\\s]+$", message = "올바른 전화번호 형식이 아닙니다.")
    @Size(max = 255, message = "전화번호는 255자 이하여야 합니다.")
    String phoneNumber,

    @NotNull(message = "약관 동의는 필수입니다.")
    Boolean agreedPrivacyPolicy,
    
    @NotBlank(message = "닉네임은 필수입니다.")
    @Size(max = 20, message = "닉네임은 20자 이하여야 합니다.")
    String nickname
    
) {
} 