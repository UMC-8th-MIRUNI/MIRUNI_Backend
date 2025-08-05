package dgu.umc_app.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;

public record KakaoLoginRequest(
    @NotBlank(message = "카카오 Access Token은 필수입니다.")
    String kakaoAccessToken
) {} 