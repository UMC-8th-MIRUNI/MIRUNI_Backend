package dgu.umc_app.domain.plan.dto.response;

import dgu.umc_app.domain.plan.entity.AiPlan;
import dgu.umc_app.domain.plan.entity.Category;
import dgu.umc_app.domain.plan.entity.Plan;
import dgu.umc_app.domain.plan.entity.Priority;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public record AiPlanDetailResponse(

        @Schema(requiredMode = Schema.RequiredMode.REQUIRED, example = "AI")
        @NotNull
        Category category,

        @Schema(description = "상위 일정 제목", example = "모바일 앱 개발")
        String title,

        @Schema(description = "마감기한", example = "2025-08-20T23:59:00")
        LocalDateTime deadline,

        @Schema(description = "일정 범위" ,example = "앱 기획, 디자인, 프론트 개발, 백엔드 개발, 배포")
        String taskRange,

        @Schema(description = "우선 순위", example = "HIGH")
        Priority priority,

        @Schema(description = "일반 세부일정 리스트")
        List<PlanDetail> plans
) implements ScheduleDetailResponse{
    public static AiPlanDetailResponse fromAiPlan(Plan plan, List<AiPlan> aiPlans) {
        String range = (aiPlans == null || aiPlans.isEmpty()) ? null : aiPlans.get(0).getTaskRange();

        List<PlanDetail> details = aiPlans == null ? List.of() :
                aiPlans.stream()
                        .sorted((a, b) -> {
                            if (a.getStepOrder() != null && b.getStepOrder() != null) {
                                return a.getStepOrder().compareTo(b.getStepOrder());
                            }
                            // fallback: 시작 시간 비교
                            return a.getScheduledStart().compareTo(b.getScheduledStart());
                        })
                        .map(ai -> new PlanDetail(
                                ai.getId(),                                 // id 포함(수정/삭제용)
                                ai.getScheduledStart().toLocalDate(),
                                ai.getDescription(),
                                ai.getExpectedDuration(),
                                ai.getScheduledStart().toLocalTime(),       // aiPlan 기준
                                ai.getScheduledEnd().toLocalTime()          // aiPlan 기준
                        ))
                        .toList();

        return new AiPlanDetailResponse(
                Category.AI,
                plan.getTitle(),
                plan.getDeadline(),
                range,
                plan.getPriority(),
                details
        );
    }
}
