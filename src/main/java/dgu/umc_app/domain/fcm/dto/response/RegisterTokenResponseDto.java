package dgu.umc_app.domain.fcm.dto.response;

public record RegisterTokenResponseDto(
        Long tokenId
) {

    public static RegisterTokenResponseDto of(Long tokenId) {
        return new RegisterTokenResponseDto(tokenId);
    }
}
