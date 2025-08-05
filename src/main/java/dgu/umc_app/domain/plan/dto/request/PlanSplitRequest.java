package dgu.umc_app.domain.plan.dto.request;

import dgu.umc_app.domain.plan.entity.PlanType;
import dgu.umc_app.domain.plan.entity.Priority;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record PlanSplitRequest (

        @Schema(description = "작업유형")
        PlanType planType,

        @Schema(description = "일정범위")
        String taskRange,

        @Schema(description = "세부 요청사항")
        String detailRequest
) { }
