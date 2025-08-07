package dgu.umc_app.domain.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenDto {
    private final String accessToken;
    private final String refreshToken;
    private final long accessTokenExp;
    private final long refreshTokenExp;
}
