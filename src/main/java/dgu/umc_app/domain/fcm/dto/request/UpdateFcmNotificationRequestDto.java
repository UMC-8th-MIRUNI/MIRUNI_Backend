package dgu.umc_app.domain.fcm.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateFcmNotificationRequestDto(

        @NotBlank
        String deviceId,

        boolean enabled
) {
}
