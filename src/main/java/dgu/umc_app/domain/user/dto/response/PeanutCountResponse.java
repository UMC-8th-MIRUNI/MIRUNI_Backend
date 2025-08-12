package dgu.umc_app.domain.user.dto.response;

import dgu.umc_app.domain.user.entity.User;

public record PeanutCountResponse(
        int peanutCount
) {
    public static PeanutCountResponse from(User user) {
        return new PeanutCountResponse(
                user.getPeanutCount()
        );
    }
}
