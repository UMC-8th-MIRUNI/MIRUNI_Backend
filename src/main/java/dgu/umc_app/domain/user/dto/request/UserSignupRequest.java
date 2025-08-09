package dgu.umc_app.domain.user.dto.request;

import dgu.umc_app.domain.user.entity.User;
import dgu.umc_app.domain.user.entity.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public record UserSignupRequest(
        @NotBlank(message = "이름은 필수입니다.")
        @Size(max = 20, message = "이름은 20자 이하여야 합니다.")
        String name,
        
        @NotBlank(message = "생년월일은 필수입니다. 예시: 1990-01-01")
        @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "올바른 생년월일 형식이 아닙니다.")
        String birthday,

        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        @Size(max = 50, message = "이메일은 50자 이하여야 합니다.")
        String email,
        
        @NotBlank(message = "전화번호는 필수입니다.")
        @Size(max = 255, message = "전화번호는 255자 이하여야 합니다.")
        String phoneNumber,
        
        @NotBlank(message = "비밀번호는 필수입니다.")
        String password,
        
        @NotBlank(message = "닉네임은 필수입니다.")
        @Size(max = 20, message = "닉네임은 20자 이하여야 합니다.")
        String nickname
        
) {
        public User toEntity(String encodedPassword) {
                return User.builder()
                        .name(name)
                        .email(email)
                        .birthday(birthday)
                        .phoneNumber(phoneNumber)
                        .password(encodedPassword)
                        .nickname(nickname)
                        .passwordExpired(false)
                        .lastPasswordChanged(LocalDateTime.now())
                        .agreedPrivacyPolicy(true)
                        .peanutCount(0)
                        .userPreference("{}")
                        .oauthProvider(null)
                        .status(Status.ACTIVE)
                        .build();
        }
}