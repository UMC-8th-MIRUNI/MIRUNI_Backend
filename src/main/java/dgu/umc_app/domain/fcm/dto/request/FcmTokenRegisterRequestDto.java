package dgu.umc_app.domain.fcm.dto.request;

import dgu.umc_app.domain.fcm.entity.FcmToken;
import dgu.umc_app.domain.user.entity.User;
import jakarta.validation.constraints.NotBlank;

public record FcmTokenRegisterRequestDto(

        @NotBlank
        String token,

        @NotBlank
        String deviceId
){
        public FcmToken toEntity(User user) {
                return  FcmToken.builder()
                                .user(user)
                                .token(this.token())
                                .deviceId(this.deviceId())
                                .isActive(true)
                                .build();
        }
}
