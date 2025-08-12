package dgu.umc_app.domain.user.dto;

import dgu.umc_app.domain.user.entity.OauthProvider;
import dgu.umc_app.domain.user.entity.Status;
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
                .name("") 
                .email(this.email)
                .phoneNumber("") 
                .password("") 
                .nickname("") 
                .passwordExpired(false)
                .lastPasswordChanged(LocalDateTime.now())
                .agreedPrivacyPolicy(true)
                .peanutCount(0)
                // userPreference 필드 제거 - UserSurvey 엔티티로 대체
                .oauthProvider(provider)
                .status(Status.PENDING) 
                .build();
    }
} 

