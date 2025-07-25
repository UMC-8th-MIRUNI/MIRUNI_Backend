package dgu.umc_app.domain.user.dto;

import dgu.umc_app.domain.user.entity.OauthProvider;
import dgu.umc_app.domain.user.entity.User;
import lombok.Builder;
import java.time.LocalDateTime;

@Builder
public record AuthUserInfoDto(
    String email, String name
) {
    public static AuthUserInfoDto of(String email, String name) {
        return AuthUserInfoDto.builder()
                .email(email)
                .name(name)
                .build();
    }

    public User toSocialUser(OauthProvider provider) {
        return User.builder()
                .name(this.name)
                .email(this.email)
                .phoneNumber("")
                .password("")
                .nickname(this.name)
                .passwordExpired(false)
                .lastPasswordChanged(LocalDateTime.now())
                .agreedPrivacyPolicy(true)
                .peanutCount(0)
                .popupAlarmInterval(60)
                .bannerAlarmInterval(60)
                .userPreference("")
                .oauthProvider(provider)
                .build();
    }
} 

