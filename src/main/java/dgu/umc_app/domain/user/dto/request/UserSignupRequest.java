package dgu.umc_app.domain.user.dto.request;

import dgu.umc_app.domain.user.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public record UserSignupRequest(
        @NotBlank(message = "이름은 필수입니다.")
        @Size(max = 20, message = "이름은 20자 이하여야 합니다.")
        String name,
        
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
        String nickname,
        
        String userPreference
) {
        public User toEntity(String encodedPassword) {
                return User.builder()
                        .name(name)
                        .email(email)
                        .phoneNumber(phoneNumber)
                        .password(encodedPassword)
                        .nickname(nickname)
                        .passwordExpired(false)
                        .lastPasswordChanged(LocalDateTime.now())
                        .agreedPrivacyPolicy(true)
                        .peanutCount(0)
                        .popupAlarmInterval(null)
                        .bannerAlarmInterval(null)
                        .userPreference(userPreference)
                        .oauthProvider(null)
                        .build();
        }
}