package dgu.umc_app.domain.review.dto.response;

import java.util.Date;

public record ReviewCountByDateResponse(
        Date date,
        Long count
) {
}
