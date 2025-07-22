package dgu.umc_app.domain.user.dto;

import lombok.Builder;

@Builder
public record GoogleUserInfoDto(
    String email, String name
) {
    public static GoogleUserInfoDto of(String email, String name) {
        return GoogleUserInfoDto.builder()
                .email(email)
                .name(name)
                .build();
    }
} 