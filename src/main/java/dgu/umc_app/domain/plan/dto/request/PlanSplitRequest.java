package dgu.umc_app.domain.plan.dto.request;

import dgu.umc_app.domain.plan.entity.PlanType;
import dgu.umc_app.domain.plan.entity.Priority;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record PlanSplitRequest (

        @Schema(description = "작업유형", example = "IMMERSIVE")
        PlanType planType,

        @Schema(description = "일정범위", example = "프로젝트 최종 발표 준비")
        String taskRange,

        @Schema(description = "세부 요청사항", example = "PPT 작성, 발표 연습, 시연 준비를 단계별로 나누어 주세요. 각 단계마다 1-2시간씩 배정해주세요.")
        String detailRequest
) { }
