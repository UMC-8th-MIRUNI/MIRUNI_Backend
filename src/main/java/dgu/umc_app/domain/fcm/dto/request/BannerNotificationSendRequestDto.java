package dgu.umc_app.domain.fcm.dto.request;

import dgu.umc_app.domain.fcm.entity.NotificationType;
import dgu.umc_app.domain.fcm.entity.ReminderType;
import jakarta.validation.constraints.NotNull;

public record BannerNotificationSendRequestDto(

        @NotNull
        NotificationType type,

        @NotNull
        Long targetId,

        @NotNull
        ReminderType reminderType

) {
}
