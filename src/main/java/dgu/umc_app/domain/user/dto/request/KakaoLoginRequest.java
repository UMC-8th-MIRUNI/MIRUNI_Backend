package dgu.umc_app.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record KakaoLoginRequest(
        @NotBlank(message = "카카오 Access Token은 필수입니다.")
        @Schema(example = "eyJhbGciOiJIUzI1NiJ9.tempTokenForSignup...")
        String kakaoAccessToken
) {} 