package dgu.umc_app.domain.plan.dto.request;

import dgu.umc_app.domain.plan.entity.Plan;
import dgu.umc_app.domain.plan.entity.PlanCategory;
import dgu.umc_app.domain.plan.entity.Priority;
import dgu.umc_app.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record PlanCreateRequest(

        @NotBlank(message = "일정 제목은 필수입니다.")
        @Size(max = 50, message = "일정 제목은 50자 이내여야 합니다.")
        @Schema(description = "일정 제목", example = "프로젝트 최종 발표 준비")
        String title,

        @NotNull(message = "마감 기한은 필수입니다.")
        @Schema(description = "마감 기한", example = "2025-09-30T18:00:00")
        LocalDateTime deadline,

        @NotNull(message = "수행 시작 날짜는 필수입니다.")
        @Schema(description = "수행 시작 날짜", example = "2025-09-20T09:00:00")
        LocalDateTime scheduledStart,

        @NotNull(message = "수행 종료 날짜는 필수입니다.")
        @Schema(description = "수행 종료 날짜", example = "2025-09-29T17:00:00")
        LocalDateTime scheduledEnd,

        @NotNull(message = "우선순위는 필수입니다.")
        @Schema(description = "우선순위", example = "HIGH")
        Priority priority,

        @NotBlank(message = "일정 내용은 필수입니다.")
        @Schema(description = "일정 간단설명", example = "팀 프로젝트 최종 발표를 위한 PPT 작성, 발표 연습, 시연 준비")
        String description

) {
        public Plan toEntity(User user) {
                return Plan.builder()
                        .title(title)
                        .description(description)
                        .deadline(deadline)
                        .scheduledStart(scheduledStart)
                        .scheduledEnd(scheduledEnd)
                        .priority(priority)
                        .isDone(false)
                        .user(user)
                        .build();
        }
}
