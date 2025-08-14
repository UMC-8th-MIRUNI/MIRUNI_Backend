package dgu.umc_app.domain.plan.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record PlanDeleteResponse(

        @Schema(description = "요청개수")
        int requested,          // 요청 개수 (BASIC=1, AI=ids.size)

        int deleted,            // 실제 삭제 개수

        List<Long> notFound,    // 없던 id

        List<Long> unauthorized // 권한/소속 불일치 id
) {
}

