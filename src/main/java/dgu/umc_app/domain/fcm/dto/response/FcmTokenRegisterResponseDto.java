package dgu.umc_app.domain.fcm.dto.response;

public record FcmTokenRegisterResponseDto(
        Long tokenId
) {

    public static FcmTokenRegisterResponseDto of(Long tokenId) {
        return new FcmTokenRegisterResponseDto(tokenId);
    }
}
