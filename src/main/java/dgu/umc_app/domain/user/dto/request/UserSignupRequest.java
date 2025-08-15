package dgu.umc_app.domain.user.dto.request;

import dgu.umc_app.domain.user.entity.User;
import dgu.umc_app.domain.user.entity.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

public record UserSignupRequest(
        @NotBlank(message = "이름은 필수입니다.")
        @Size(max = 20, message = "이름은 20자 이하여야 합니다.")
        @Schema(description = "사용자 실명", example = "추상윤")
        String name,

        @NotBlank(message = "생년월일은 필수입니다. 예시: 1990-01-01")
        @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "올바른 생년월일 형식이 아닙니다.")
        @Schema(description = "생년월일 (YYYY-MM-DD 형식)", example = "2003-03-03")
        String birthday,

        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        @Size(max = 50, message = "이메일은 50자 이하여야 합니다.")
        @Schema(description = "이메일 주소", example = "dhzktldh@gmail.com")
        String email,

        @NotBlank(message = "전화번호는 필수입니다.")
        @Size(max = 255, message = "전화번호는 255자 이하여야 합니다.")
        @Schema(description = "전화번호", example = "010-7689-3141")
        String phoneNumber,

        @NotBlank(message = "비밀번호는 필수입니다.")
        @Schema(description = "비밀번호", example = "password123!")
        String password,

        @NotBlank(message = "닉네임은 필수입니다.")
        @Size(max = 20, message = "닉네임은 20자 이하여야 합니다.")
        @Schema(description = "사용자 닉네임", example = "추추")
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
                        // userPreference 필드 제거
                        .oauthProvider(null)
                        .status(Status.PENDING)
                        .build();
        }
}
